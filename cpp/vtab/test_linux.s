    .file    "test.cpp"
    .version    "01.01"
gcc2_compiled.:
.globl table
.data
    .align 4
    .type     table,@object
    .size     table,4
table:
    .long    0
.globl __rethrow
.text
    .align 4
.globl createTable__Fv
    .type     createTable__Fv,@function
createTable__Fv:
.LFB1:
    pushl    %ebp
.LCFI0:
    movl    %esp, %ebp
.LCFI1:
    subl    $24, %esp
.LCFI2:
    subl    $12, %esp
    pushl    $40
.LCFI3:
    call    __builtin_vec_new
    addl    $16, %esp
    movl    %eax, -16(%ebp)
    movb    $1, -17(%ebp)
.LEHB3:
    movl    -16(%ebp), %eax
    movl    %eax, %eax
    movl    %eax, -4(%ebp)
    movl    -4(%ebp), %eax
    movl    %eax, -8(%ebp)
    movl    $4, -12(%ebp)
    cmpl    $-1, -12(%ebp)
    je    .L4
    .p2align 2
.L5:
    subl    $12, %esp
    pushl    -8(%ebp)
    call    __8employee
    addl    $16, %esp
    movl    %eax, %eax
    leal    -8(%ebp), %eax
    addl    $8, (%eax)
    movl    -8(%ebp), %eax
    leal    -12(%ebp), %eax
    decl    (%eax)
    cmpl    $-1, -12(%ebp)
    jne    .L5
.L4:
    movl    -4(%ebp), %eax
    movb    $0, -17(%ebp)
    movl    %eax, %eax
    movl    %eax, table
.LEHE3:
    cmpb    $0, -17(%ebp)
    je    .L2
    subl    $12, %esp
    pushl    -16(%ebp)
    call    __builtin_vec_delete
    addl    $16, %esp
    jmp    .L2
    .p2align 2
.L3:
    cmpb    $0, -17(%ebp)
    je    .L10
    subl    $12, %esp
    pushl    -16(%ebp)
    call    __builtin_vec_delete
    addl    $16, %esp
.L10:
    subl    $12, %esp
    pushl    $.LRTH3
    call    __rethrow
    .p2align 2
.L2:
    leave
    ret
.LFE1:
.Lfe1:
    .size     createTable__Fv,.Lfe1-createTable__Fv
    .align 4
.globl reportSal__Fv
    .type     reportSal__Fv,@function
reportSal__Fv:
.LFB2:
    pushl    %ebp
.LCFI4:
    movl    %esp, %ebp
.LCFI5:
    subl    $8, %esp
.LCFI6:
    movl    $0, -4(%ebp)
    .p2align 2
.L17:
    cmpl    $4, -4(%ebp)
    jle    .L20
    jmp    .L16
    .p2align 2
.L20:
    subl    $12, %esp
    movl    -4(%ebp), %eax
    imull    $8, %eax, %edx
    movl    table, %eax
    movl    4(%eax,%edx), %edx
    addl    $8, %edx
    movl    -4(%ebp), %eax
    sall    $3, %eax
    addl    table, %eax
    pushl    %eax
    movl    (%edx), %eax
.LCFI7:
    call    *%eax
    fstp    %st(0)
    addl    $16, %esp
    leal    -4(%ebp), %eax
    incl    (%eax)
    jmp    .L17
    .p2align 2
.L16:
    leave
    ret
.LFE2:
.Lfe2:
    .size     reportSal__Fv,.Lfe2-reportSal__Fv
    .section    .gnu.linkonce.t.__8employee,"ax",@progbits
    .align 4
    .weak    __8employee
    .type     __8employee,@function
__8employee:
.LFB3:
    pushl    %ebp
.LCFI8:
    movl    %esp, %ebp
.LCFI9:
    movl    8(%ebp), %eax
    movl    $__vt_8employee, 4(%eax)
    popl    %ebp
    ret
.LFE3:
.Lfe3:
    .size     __8employee,.Lfe3-__8employee
    .weak    __vt_8employee
    .section    .gnu.linkonce.d.__vt_8employee,"aw",@progbits
    .align 8
    .type     __vt_8employee,@object
    .size     __vt_8employee,16
__vt_8employee:
    .long    0
    .long    __tf8employee
    .long    sal__8employee
    .zero    4
    .comm    __ti8employee,8,4
        .section    .rodata
.LC1:
    .string    "8employee"
    .align 4
.LC2:
    .long    0x41a00000
    .section    .gnu.linkonce.t.sal__8employee,"ax",@progbits
    .align 4
    .weak    sal__8employee
    .type     sal__8employee,@function
sal__8employee:
.LFB4:
    pushl    %ebp
.LCFI10:
    movl    %esp, %ebp
.LCFI11:
    movl    8(%ebp), %eax
    flds    .LC2
    popl    %ebp
    ret
.LFE4:
.Lfe4:
    .size     sal__8employee,.Lfe4-sal__8employee
    .section    .gnu.linkonce.t.__tf8employee,"ax",@progbits
    .align 4
    .weak    __tf8employee
    .type     __tf8employee,@function
__tf8employee:
.LFB5:
    pushl    %ebp
.LCFI12:
    movl    %esp, %ebp
.LCFI13:
    subl    $8, %esp
.LCFI14:
    cmpl    $0, __ti8employee
    jne    .L25
    subl    $8, %esp
    pushl    $.LC1
    pushl    $__ti8employee
.LCFI15:
    call    __rtti_user
    addl    $16, %esp
.L25:
    movl    $__ti8employee, %eax
    leave
    ret
.LFE5:
.Lfe5:
    .size     __tf8employee,.Lfe5-__tf8employee
    .section    .gcc_except_table,"aw",@progbits
    .align 4
__EXCEPTION_TABLE__:
    .long    -2
    .value    4
    .value    1
.LRTH0:
.LRTH3:
    .long    .LEHB3
    .long    .LEHE3
    .long    .L3
    .long    0

.LRTH1:
    .long    -1


    .section    .eh_frame,"aw",@progbits
__FRAME_BEGIN__:
    .4byte    .LLCIE1
.LSCIE1:
    .4byte    0x0
    .byte    0x1
    .string    "eh"

    .4byte    __EXCEPTION_TABLE__
    .byte    0x1
    .byte    0x7c
    .byte    0x8
    .byte    0xc
    .byte    0x4
    .byte    0x4
    .byte    0x88
    .byte    0x1
    .align 4
.LECIE1:
    .set    .LLCIE1,.LECIE1-.LSCIE1
    .4byte    .LLFDE1
.LSFDE1:
    .4byte    .LSFDE1-__FRAME_BEGIN__
    .4byte    .LFB1
    .4byte    .LFE1-.LFB1
    .byte    0x4
    .4byte    .LCFI0-.LFB1
    .byte    0xe
    .byte    0x8
    .byte    0x85
    .byte    0x2
    .byte    0x4
    .4byte    .LCFI1-.LCFI0
    .byte    0xd
    .byte    0x5
    .byte    0x4
    .4byte    .LCFI3-.LCFI1
    .byte    0x2e
    .byte    0x10
    .align 4
.LEFDE1:
    .set    .LLFDE1,.LEFDE1-.LSFDE1
    .4byte    .LLFDE3
.LSFDE3:
    .4byte    .LSFDE3-__FRAME_BEGIN__
    .4byte    .LFB2
    .4byte    .LFE2-.LFB2
    .byte    0x4
    .4byte    .LCFI4-.LFB2
    .byte    0xe
    .byte    0x8
    .byte    0x85
    .byte    0x2
    .byte    0x4
    .4byte    .LCFI5-.LCFI4
    .byte    0xd
    .byte    0x5
    .byte    0x4
    .4byte    .LCFI7-.LCFI5
    .byte    0x2e
    .byte    0x10
    .align 4
.LEFDE3:
    .set    .LLFDE3,.LEFDE3-.LSFDE3
    .ident    "GCC: (GNU) 2.96 20000731 (Red Hat Linux 7.1 2.96-81)"
