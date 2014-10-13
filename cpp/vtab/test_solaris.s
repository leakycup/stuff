    .file    "test.cpp"
gcc2_compiled.:
    .global table
.section    ".data"
    .align 4
    .type     table,#object
    .size     table,4
table:
    .uaword    0
    .global __throw
.section    ".text"
    .align 4
    .global createTable__Fv
    .type     createTable__Fv,#function
    .proc    020
createTable__Fv:
.LLFB1:
    !#PROLOGUE# 0
    save    %sp, -112, %sp
.LLCFI0:
    !#PROLOGUE# 1
    sethi    %hi(table), %o0
    or    %o0, %lo(table), %l3
    mov    40, %o0
    call    __builtin_vec_new, 0
     nop
    mov    %o0, %l1
    mov    1, %l2
.LLEHB9:
    mov    %l1, %l0
    mov    %l0, %l4
    mov    %l0, %l6
    mov    4, %l5
    cmp    %l5, -1
    be    .LL11
    nop
.LL12:
    mov    %l6, %o0
    call    __8employee, 0
     nop
    add    %l6, 8, %l6
.LL14:
    add    %l5, -1, %l5
    cmp    %l5, -1
    bne    .LL17
    nop
    b    .LL11
     nop
.LL17:
    b    .LL12
     nop
.LL13:
.LL11:
    mov    %l4, %o0
    mov    0, %l2
    st    %o0, [%l3]
.LLEHE9:
    b    .LL18
     nop
.LL10:
    call    __throw, 0
     nop
.LL18:
    cmp    %l2, 0
    be    .LL20
    nop
    mov    %l1, %o0
    call    __builtin_vec_delete, 0
     nop
    b    .LL20
     nop
.LL19:
.LL20:
    b    .LL8
     nop
    b    .LL23
     nop
.LLEHB24:
.LL9:
    cmp    %l2, 0
    be    .LL22
    nop
    mov    %l1, %o0
    call    __builtin_vec_delete, 0
     nop
    b    .LL22
     nop
.LL21:
.LL22:
    b    .LL10
     nop
.LLEHE24:
    b    .LL24
     nop
.LL25:
    call    __throw, 0
     nop
.LL26:
.LL24:
    call    terminate__Fv, 0
     nop
.LL23:
.LL8:
    ret
    restore
.LLFE1:
.LLfe1:
    .size     createTable__Fv,.LLfe1-createTable__Fv
    .align 4
    .global reportSal__Fv
    .type     reportSal__Fv,#function
    .proc    020
reportSal__Fv:
.LLFB2:
    !#PROLOGUE# 0
    save    %sp, -120, %sp
.LLCFI1:
    !#PROLOGUE# 1
    st    %g0, [%fp-20]
.LL28:
    ld    [%fp-20], %o0
    cmp    %o0, 4
    ble    .LL31
    nop
    b    .LL29
     nop
.LL31:
    ld    [%fp-20], %o0
    mov    %o0, %o1
    sll    %o1, 3, %o0
    sethi    %hi(table), %o1
    or    %o1, %lo(table), %o2
    ld    [%o2], %o1
    add    %o0, %o1, %o0
    ld    [%o0+4], %o1
    add    %o1, 8, %l0
    sethi    %hi(table), %o1
    or    %o1, %lo(table), %o0
    ld    [%fp-20], %o1
    mov    %o1, %o2
    sll    %o2, 3, %o1
    ld    [%o0], %o2
    add    %o1, %o2, %o0
    lduh    [%l0], %o1
    sll    %o1, 16, %o2
    sra    %o2, 16, %o1
    add    %o0, %o1, %o0
    ld    [%l0+4], %o1
    call    %o1, 0
     nop
.LL30:
    ld    [%fp-20], %o0
    add    %o0, 1, %o1
    st    %o1, [%fp-20]
    b    .LL28
     nop
.LL29:
    b    .LL27
     nop
.LL27:
    ret
    restore
.LLFE2:
.LLfe2:
    .size     reportSal__Fv,.LLfe2-reportSal__Fv
    .common    __ti8employee,8,8
.section    ".rodata"
    .align 8
.LLC3:
    .asciz    "8employee"
.section    ".gnu.linkonce.t.__tf8employee",#alloc,#execinstr
    .align 4
    .weak    __tf8employee
    .type     __tf8employee,#function
    .proc    0110
__tf8employee:
.LLFB3:
    !#PROLOGUE# 0
    save    %sp, -112, %sp
.LLCFI2:
    !#PROLOGUE# 1
    sethi    %hi(__ti8employee), %o1
    or    %o1, %lo(__ti8employee), %o0
    ld    [%o0], %o1
    cmp    %o1, 0
    bne    .LL33
    nop
    sethi    %hi(__ti8employee), %o1
    or    %o1, %lo(__ti8employee), %o0
    sethi    %hi(.LLC3), %o2
    or    %o2, %lo(.LLC3), %o1
    call    __rtti_user, 0
     nop
.LL33:
    sethi    %hi(__ti8employee), %o0
    or    %o0, %lo(__ti8employee), %i0
    b    .LL32
     nop
.LL32:
    ret
    restore
.LLFE3:
.LLfe3:
    .size     __tf8employee,.LLfe3-__tf8employee
    .common    __ti3ceo,12,8
.section    ".rodata"
    .align 8
.LLC4:
    .asciz    "3ceo"
.section    ".gnu.linkonce.t.__tf3ceo",#alloc,#execinstr
    .align 4
    .weak    __tf3ceo
    .type     __tf3ceo,#function
    .proc    0110
__tf3ceo:
.LLFB4:
    !#PROLOGUE# 0
    save    %sp, -112, %sp
.LLCFI3:
    !#PROLOGUE# 1
    sethi    %hi(__ti3ceo), %o1
    or    %o1, %lo(__ti3ceo), %o0
    ld    [%o0], %o1
    cmp    %o1, 0
    bne    .LL35
    nop
    call    __tf8employee, 0
     nop
    sethi    %hi(__ti3ceo), %o1
    or    %o1, %lo(__ti3ceo), %o0
    sethi    %hi(.LLC4), %o2
    or    %o2, %lo(.LLC4), %o1
    sethi    %hi(__ti8employee), %o3
    or    %o3, %lo(__ti8employee), %o2
    call    __rtti_si, 0
     nop
.LL35:
    sethi    %hi(__ti3ceo), %o0
    or    %o0, %lo(__ti3ceo), %i0
    b    .LL34
     nop
.LL34:
    ret
    restore
.LLFE4:
.LLfe4:
    .size     __tf3ceo,.LLfe4-__tf3ceo
    .common    __ti7manager,12,8
.section    ".rodata"
    .align 8
.LLC5:
    .asciz    "7manager"
.section    ".gnu.linkonce.t.__tf7manager",#alloc,#execinstr
    .align 4
    .weak    __tf7manager
    .type     __tf7manager,#function
    .proc    0110
__tf7manager:
.LLFB5:
    !#PROLOGUE# 0
    save    %sp, -112, %sp
.LLCFI4:
    !#PROLOGUE# 1
    sethi    %hi(__ti7manager), %o1
    or    %o1, %lo(__ti7manager), %o0
    ld    [%o0], %o1
    cmp    %o1, 0
    bne    .LL37
    nop
    call    __tf8employee, 0
     nop
    sethi    %hi(__ti7manager), %o1
    or    %o1, %lo(__ti7manager), %o0
    sethi    %hi(.LLC5), %o2
    or    %o2, %lo(.LLC5), %o1
    sethi    %hi(__ti8employee), %o3
    or    %o3, %lo(__ti8employee), %o2
    call    __rtti_si, 0
     nop
.LL37:
    sethi    %hi(__ti7manager), %o0
    or    %o0, %lo(__ti7manager), %i0
    b    .LL36
     nop
.LL36:
    ret
    restore
.LLFE5:
.LLfe5:
    .size     __tf7manager,.LLfe5-__tf7manager
.section    ".gnu.linkonce.t.__8employee",#alloc,#execinstr
    .align 4
    .weak    __8employee
    .type     __8employee,#function
    .proc    0110
__8employee:
.LLFB6:
    !#PROLOGUE# 0
    save    %sp, -112, %sp
.LLCFI5:
    !#PROLOGUE# 1
    mov    %i0, %o0
    sethi    %hi(_vt.8employee), %o2
    or    %o2, %lo(_vt.8employee), %o1
    st    %o1, [%o0+4]
.LL16:
    mov    %o0, %i0
    b    .LL15
     nop
.LL15:
    ret
    restore
.LLFE6:
.LLfe6:
    .size     __8employee,.LLfe6-__8employee
    .weak    _vt.8employee
.section    ".gnu.linkonce.d._vt.8employee",#alloc,#write
    .align 8
    .type     _vt.8employee,#object
    .size     _vt.8employee,24
_vt.8employee:
    .uahalf    0
    .uahalf    0
    .uaword    __tf8employee
    .uahalf    0
    .uahalf    0
    .uaword    sal__8employee
    .skip 8
.section    ".rodata"
    .align 4
.LLC6:
    .uaword    0x41a00000 ! ~2.00000000000000000000e1
.section    ".gnu.linkonce.t.sal__8employee",#alloc,#execinstr
    .align 4
    .weak    sal__8employee
    .type     sal__8employee,#function
    .proc    06
sal__8employee:
.LLFB7:
    !#PROLOGUE# 0
    save    %sp, -112, %sp
.LLCFI6:
    !#PROLOGUE# 1
    mov    %i0, %o0
    sethi    %hi(.LLC6), %o2
    or    %o2, %lo(.LLC6), %o1
    ld    [%o1], %f0
    b    .LL2
     nop
    b    .LL3
     nop
    b    .LL2
     nop
.LL3:
.LL2:
    ret
    restore
.LLFE7:
.LLfe7:
    .size     sal__8employee,.LLfe7-sal__8employee
.section    ".gcc_except_table",#alloc,#write
    .align 4
__EXCEPTION_TABLE__:
    .uaword    .LLEHB9
    .uaword    .LLEHE9
    .uaword    .LL9

    .uaword    .LLEHB24
    .uaword    .LLEHE24
    .uaword    .LL24

.LLRTH1:
    .uaword    -1
    .uaword    -1


.section    ".eh_frame",#alloc,#write
__FRAME_BEGIN__:
    .uaword    .LLECIE1-.LLSCIE1
.LLSCIE1:
    .uaword    0x0
    .byte    0x1
    .asciz    "eh"

    .uaword    __EXCEPTION_TABLE__
    .byte    0x1
    .byte    0x7c
    .byte    0x65
    .byte    0xc
    .byte    0xe
    .byte    0x0
    .byte    0x9
    .byte    0x65
    .byte    0xf
    .align 4
.LLECIE1:
    .uaword    .LLEFDE1-.LLSFDE1
.LLSFDE1:
    .uaword    .LLSFDE1-__FRAME_BEGIN__
    .uaword    .LLFB1
    .uaword    .LLFE1-.LLFB1
    .byte    0x4
    .uaword    .LLCFI0-.LLFB1
    .byte    0xd
    .byte    0x1e
    .byte    0x2d
    .byte    0x9
    .byte    0x65
    .byte    0x1f
    .align 4
.LLEFDE1:
    .uaword    .LLEFDE3-.LLSFDE3
.LLSFDE3:
    .uaword    .LLSFDE3-__FRAME_BEGIN__
    .uaword    .LLFB2
    .uaword    .LLFE2-.LLFB2
    .byte    0x4
    .uaword    .LLCFI1-.LLFB2
    .byte    0xd
    .byte    0x1e
    .byte    0x2d
    .byte    0x9
    .byte    0x65
    .byte    0x1f
    .align 4
.LLEFDE3:
    .uaword    .LLEFDE5-.LLSFDE5
.LLSFDE5:
    .uaword    .LLSFDE5-__FRAME_BEGIN__
    .uaword    .LLFB3
    .uaword    .LLFE3-.LLFB3
    .byte    0x4
    .uaword    .LLCFI2-.LLFB3
    .byte    0xd
    .byte    0x1e
    .byte    0x2d
    .byte    0x9
    .byte    0x65
    .byte    0x1f
    .align 4
.LLEFDE5:
    .uaword    .LLEFDE7-.LLSFDE7
.LLSFDE7:
    .uaword    .LLSFDE7-__FRAME_BEGIN__
    .uaword    .LLFB4
    .uaword    .LLFE4-.LLFB4
    .byte    0x4
    .uaword    .LLCFI3-.LLFB4
    .byte    0xd
    .byte    0x1e
    .byte    0x2d
    .byte    0x9
    .byte    0x65
    .byte    0x1f
    .align 4
.LLEFDE7:
    .uaword    .LLEFDE9-.LLSFDE9
.LLSFDE9:
    .uaword    .LLSFDE9-__FRAME_BEGIN__
    .uaword    .LLFB5
    .uaword    .LLFE5-.LLFB5
    .byte    0x4
    .uaword    .LLCFI4-.LLFB5
    .byte    0xd
    .byte    0x1e
    .byte    0x2d
    .byte    0x9
    .byte    0x65
    .byte    0x1f
    .align 4
.LLEFDE9:
    .uaword    .LLEFDE11-.LLSFDE11
.LLSFDE11:
    .uaword    .LLSFDE11-__FRAME_BEGIN__
    .uaword    .LLFB6
    .uaword    .LLFE6-.LLFB6
    .byte    0x4
    .uaword    .LLCFI5-.LLFB6
    .byte    0xd
    .byte    0x1e
    .byte    0x2d
    .byte    0x9
    .byte    0x65
    .byte    0x1f
    .align 4
.LLEFDE11:
    .uaword    .LLEFDE13-.LLSFDE13
.LLSFDE13:
    .uaword    .LLSFDE13-__FRAME_BEGIN__
    .uaword    .LLFB7
    .uaword    .LLFE7-.LLFB7
    .byte    0x4
    .uaword    .LLCFI6-.LLFB7
    .byte    0xd
    .byte    0x1e
    .byte    0x2d
    .byte    0x9
    .byte    0x65
    .byte    0x1f
    .align 4
.LLEFDE13:
    .ident    "GCC: (GNU) 2.95.2 19991024 (release)"
