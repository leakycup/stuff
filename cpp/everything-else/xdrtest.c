/* a small program that shows how to use xdr directly.
 * needs to be linked with libnsl (Network Services Library) on sunos.
 */
#include <rpc/xdr.h>

int main (void)
{
    XDR *x;
    caddr_t buf;
    unsigned int buf_len;
    int res_int, arg_int;
    float res_float, arg_float;
    double res_double, arg_double;
    double *res_double_p = NULL;

    arg_int = 6666667;
    arg_float = 66688667.73821323;
    arg_double = 66688667.73821323;

    x = (XDR *)malloc(sizeof(XDR));
    buf_len = xdr_sizeof(xdr_double, &arg_double);
    buf = (caddr_t)malloc(buf_len);
    xdrmem_create(x, buf, buf_len, XDR_ENCODE);

    if (!xdr_double(x, &arg_double)) {
	printf("encode failed\n");
	return (-1);
    }

    x->x_op = XDR_DECODE;
    XDR_SETPOS(x, 0);
    xdr_double(x, res_double_p);

    printf("sizeof(arg): %d arg = %f\n", sizeof(arg_double), arg_double);
    printf("buf_len: %d res = %f\n", buf_len, *res_double_p);

    xdr_destroy(x);
    free(x);
    free(buf);  /* use xdr_free() ? */
    xdr_free(xdr_double, res_double_p);

    return (0);
}

