	.file	"caller.c"
	.version	"01.01"
gcc2_compiled.:
		.section	.rodata
.LC0:
	.string	"caller : i = %lld j = %lld\n"
.LC1:
	.string	"caller : k = %ld l = %ld\n"
.LC2:
	.string	"caller : m = %hd n = %hd\n"
.LC3:
	.string	"caller : o = %c p = %c\n"
.text
	.align 4
.globl caller
	.type	 caller,@function
caller:
	pushl	%ebp
	movl	%esp, %ebp
	subl	$40, %esp
	movl	$0, -8(%ebp)
	movl	$0, -4(%ebp)
	movl	$1, -16(%ebp)
	movl	$0, -12(%ebp)
	movl	$2, -20(%ebp)
	movl	$3, -24(%ebp)
	movw	$4, -26(%ebp)
	movw	$5, -28(%ebp)
	movb	$6, -29(%ebp)
	movb	$7, -30(%ebp)
	subl	$12, %esp
	pushl	-12(%ebp)
	pushl	-16(%ebp)
	pushl	-4(%ebp)
	pushl	-8(%ebp)
	pushl	$.LC0
	call	printf
	addl	$32, %esp
	pushl	-12(%ebp)
	pushl	-16(%ebp)
	pushl	-4(%ebp)
	pushl	-8(%ebp)
	call	callee1
	addl	$16, %esp
	subl	$4, %esp
	pushl	-24(%ebp)
	pushl	-20(%ebp)
	pushl	$.LC1
	call	printf
	addl	$16, %esp
	subl	$8, %esp
	pushl	-24(%ebp)
	pushl	-20(%ebp)
	call	callee2
	addl	$16, %esp
	subl	$4, %esp
	movswl	-28(%ebp),%eax
	pushl	%eax
	movswl	-26(%ebp),%eax
	pushl	%eax
	pushl	$.LC2
	call	printf
	addl	$16, %esp
	subl	$8, %esp
	movswl	-28(%ebp),%eax
	pushl	%eax
	movswl	-26(%ebp),%eax
	pushl	%eax
	call	callee3
	addl	$16, %esp
	subl	$4, %esp
	movsbl	-30(%ebp),%eax
	addl	$48, %eax
	pushl	%eax
	movsbl	-29(%ebp),%eax
	addl	$48, %eax
	pushl	%eax
	pushl	$.LC3
	call	printf
	addl	$16, %esp
	subl	$8, %esp
	movsbl	-30(%ebp),%eax
	pushl	%eax
	movsbl	-29(%ebp),%eax
	pushl	%eax
	call	callee4
	addl	$16, %esp
	subl	$4, %esp
	movsbl	-30(%ebp),%eax
	addl	$48, %eax
	pushl	%eax
	movsbl	-29(%ebp),%eax
	addl	$48, %eax
	pushl	%eax
	pushl	$.LC3
	call	printf
	addl	$16, %esp
	subl	$8, %esp
	movsbl	-30(%ebp),%eax
	pushl	%eax
	movsbl	-29(%ebp),%eax
	pushl	%eax
	call	callee3
	addl	$16, %esp
	subl	$4, %esp
	movswl	-28(%ebp),%eax
	pushl	%eax
	movswl	-26(%ebp),%eax
	pushl	%eax
	pushl	$.LC2
	call	printf
	addl	$16, %esp
	subl	$8, %esp
	movswl	-28(%ebp),%eax
	pushl	%eax
	movswl	-26(%ebp),%eax
	pushl	%eax
	call	callee2
	addl	$16, %esp
	subl	$4, %esp
	pushl	-24(%ebp)
	pushl	-20(%ebp)
	pushl	$.LC1
	call	printf
	addl	$16, %esp
	movl	-24(%ebp), %eax
	cltd
	pushl	%edx
	pushl	%eax
	movl	-20(%ebp), %eax
	cltd
	pushl	%edx
	pushl	%eax
	call	callee1
	addl	$16, %esp
	subl	$12, %esp
	pushl	-12(%ebp)
	pushl	-16(%ebp)
	pushl	-4(%ebp)
	pushl	-8(%ebp)
	pushl	$.LC0
	call	printf
	addl	$32, %esp
	subl	$8, %esp
	pushl	-16(%ebp)
	pushl	-8(%ebp)
	call	callee2
	addl	$16, %esp
	subl	$4, %esp
	pushl	-24(%ebp)
	pushl	-20(%ebp)
	pushl	$.LC1
	call	printf
	addl	$16, %esp
	subl	$8, %esp
	movswl	-24(%ebp),%eax
	pushl	%eax
	movswl	-20(%ebp),%eax
	pushl	%eax
	call	callee3
	addl	$16, %esp
	subl	$4, %esp
	movswl	-28(%ebp),%eax
	pushl	%eax
	movswl	-26(%ebp),%eax
	pushl	%eax
	pushl	$.LC2
	call	printf
	addl	$16, %esp
	subl	$8, %esp
	movsbl	-28(%ebp),%eax
	pushl	%eax
	movsbl	-26(%ebp),%eax
	pushl	%eax
	call	callee4
	addl	$16, %esp
	movl	$0, %eax
	leave
	ret
.Lfe1:
	.size	 caller,.Lfe1-caller
	.ident	"GCC: (GNU) 2.96 20000731 (Red Hat Linux 7.1 2.96-98)"
