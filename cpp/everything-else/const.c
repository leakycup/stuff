//what happens if you try to change a const?

const int I = 0;

int main(void)
{
    I = 5; //gcc warning as well as seg-fault

    return 0;
}

