#include <iostream>
using namespace std;

template <typename U>
bool is_polymorphic(U* p)
{
   bool result = false;
   typeid(result=true, *p);
   return result;
}

class base {
public:
    virtual ~base() {};
};

class derived : public base {
};

class whatever {
};

int main(void) {
    derived d;
    base *pb = &d;
    whatever w;

    cout << "is_polymorphic(pb): " << is_polymorphic(pb) << "\n";
    cout << "is_polymorphic(&d): " << is_polymorphic(&d) << "\n";
    cout << "is_polymorphic(&w): " << is_polymorphic(&w) << "\n";

    cout << "typeid(pb): " << typeid(pb).name() << "\n";
    cout << "typeid(&d): " << typeid(&d).name() << "\n";
    cout << "typeid(&w): " << typeid(&w).name() << "\n";

    cout << "typeid(base): " << typeid(base).name() << "\n";
    cout << "typeid(derived): " << typeid(derived).name() << "\n";
    cout << "typeid(whatever): " << typeid(whatever).name() << "\n";
    cout << "typeid(int): " << typeid(int).name() << "\n";

    cout << "typeid(base *): " << typeid(base *).name() << "\n";
    cout << "typeid(derived *): " << typeid(derived *).name() << "\n";
    cout << "typeid(whatever *): " << typeid(whatever *).name() << "\n";
    cout << "typeid(int *): " << typeid(int *).name() << "\n";

    return 0;
}

