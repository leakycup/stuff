	.file	"callee.c"
	.version	"01.01"
gcc2_compiled.:
.globl I
.data
	.align 8
	.type	 I,@object
	.size	 I,8
I:
	.long	0
	.long	0
.globl J
	.align 8
	.type	 J,@object
	.size	 J,8
J:
	.long	0
	.long	0
.globl K
	.align 4
	.type	 K,@object
	.size	 K,4
K:
	.long	0
.globl L
	.align 4
	.type	 L,@object
	.size	 L,4
L:
	.long	0
.globl M
	.align 2
	.type	 M,@object
	.size	 M,2
M:
	.value	0
.globl N
	.align 2
	.type	 N,@object
	.size	 N,2
N:
	.value	0
.globl O
	.type	 O,@object
	.size	 O,1
O:
.byte	0
.globl P
	.type	 P,@object
	.size	 P,1
P:
.byte	0
		.section	.rodata
.LC0:
	.string	"callee1 : I = %lld J = %lld\n"
.text
	.align 4
.globl callee1
	.type	 callee1,@function
callee1:
	pushl	%ebp
	movl	%esp, %ebp
	subl	$24, %esp
	movl	8(%ebp), %eax
	movl	12(%ebp), %edx
	movl	%eax, -8(%ebp)
	movl	%edx, -4(%ebp)
	movl	16(%ebp), %eax
	movl	20(%ebp), %edx
	movl	%eax, -16(%ebp)
	movl	%edx, -12(%ebp)
	movl	-8(%ebp), %eax
	movl	-4(%ebp), %edx
	movl	%eax, I
	movl	%edx, I+4
	movl	-16(%ebp), %eax
	movl	-12(%ebp), %edx
	movl	%eax, J
	movl	%edx, J+4
	subl	$12, %esp
	pushl	J+4
	pushl	J
	pushl	I+4
	pushl	I
	pushl	$.LC0
	call	printf
	addl	$32, %esp
	movl	$0, %eax
	leave
	ret
.Lfe1:
	.size	 callee1,.Lfe1-callee1
		.section	.rodata
.LC1:
	.string	"callee2 : K = %ld L = %ld\n"
.text
	.align 4
.globl callee2
	.type	 callee2,@function
callee2:
	pushl	%ebp
	movl	%esp, %ebp
	subl	$8, %esp
	movl	8(%ebp), %eax
	movl	%eax, K
	movl	12(%ebp), %eax
	movl	%eax, L
	subl	$4, %esp
	pushl	L
	pushl	K
	pushl	$.LC1
	call	printf
	addl	$16, %esp
	movl	$0, %eax
	leave
	ret
.Lfe2:
	.size	 callee2,.Lfe2-callee2
		.section	.rodata
.LC2:
	.string	"callee3 : M = %hd N = %hd\n"
.text
	.align 4
.globl callee3
	.type	 callee3,@function
callee3:
	pushl	%ebp
	movl	%esp, %ebp
	subl	$8, %esp
	movl	8(%ebp), %eax
	movl	12(%ebp), %edx
	movw	%ax, -2(%ebp)
	movw	%dx, -4(%ebp)
	movw	-2(%ebp), %ax
	movw	%ax, M
	movl	-4(%ebp), %eax
	movw	%ax, N
	subl	$4, %esp
	movswl	N,%eax
	pushl	%eax
	movswl	M,%eax
	pushl	%eax
	pushl	$.LC2
	call	printf
	addl	$16, %esp
	movl	$0, %eax
	leave
	ret
.Lfe3:
	.size	 callee3,.Lfe3-callee3
		.section	.rodata
.LC3:
	.string	"callee4 : O = %c P = %c\n"
.text
	.align 4
.globl callee4
	.type	 callee4,@function
callee4:
	pushl	%ebp
	movl	%esp, %ebp
	subl	$8, %esp
	movl	8(%ebp), %eax
	movl	12(%ebp), %edx
	movb	%al, -1(%ebp)
	movb	%dl, -2(%ebp)
	movb	-1(%ebp), %al
	movb	%al, O
	movb	-2(%ebp), %al
	movb	%al, P
	subl	$4, %esp
	movsbl	P,%eax
	addl	$48, %eax
	pushl	%eax
	movsbl	O,%eax
	addl	$48, %eax
	pushl	%eax
	pushl	$.LC3
	call	printf
	addl	$16, %esp
	movl	$0, %eax
	leave
	ret
.Lfe4:
	.size	 callee4,.Lfe4-callee4
	.ident	"GCC: (GNU) 2.96 20000731 (Red Hat Linux 7.1 2.96-98)"
