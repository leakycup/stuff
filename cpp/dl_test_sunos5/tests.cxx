#include <iostream>
#include "tests.h"

helloWorld::~helloWorld() {
     _count = 0;
}

helloWorld::helloWorld() {
     _count = 0;
}

helloWorld::helloWorld(int init) {
     _count = init;
}

void helloWorld::printHello() {
    std::cout << "Hello World: " << _count++ << std::endl;
}
