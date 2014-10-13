//an illustration of C++ VTab.
//see the generated assembly files.


#include "includes.h"

employee *table = NULL;

void createTable(void)
{
    table = new employee[5];
    return;
}

void reportSal(void)
{
    for (int i = 0; i < 5; i++)
        (table + i)->sal();
    return;
}

