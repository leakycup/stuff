#include <iostream>
#include <string>
using namespace std;

class person {
public:
	person() {cout << "constructing person: " << this << "\n";}
	virtual ~person() {cout << "destroying person: " << this << "\n";} //virtual destructor
};

class fat : public person {
public:
	fat() {cout << "constructing fat: " << this << "\n";}
	fat(const string& s) {cout << "constructing fat with string: " << s << " at: " << this << "\n";}
	~fat() {cout << "destroying fat: " << this << "\n";} //non-virtual destructor
};

class obese : public fat {
public:
	float weight;
	obese() : fat() {cout << "constructing obese: " << this << "\n";}
	obese(const string& s) {cout << "constructing obese with string: " << s << " at: " << this << "\n";}
	obese(const char *s) : fat(string(s)) {cout << "constructing obese with char ptr: " << s << " at: " << this << "\n";}
	obese(float w) : weight(w) {cout << "constructing obese with float value: " << w << " at: " << this << "\n";}
	~obese() {cout << "destroying obese: " << this << "\n";} //non-virtual destructor
};

class animal {
public:
	fat f;
	animal() : f("fat animal") {cout << "constructing animal: " << this << "\n";}
	//no destructor specified
};

class reptile : public animal {
public:
	reptile() {cout << "constructing reptile: " << this << "\n";}
	~reptile() {cout << "destroying reptile: " << this << "\n";} //non-virtual destructor
};

class snake : public reptile {
public:
	snake() {cout << "constructing snake: " << this << "\n";}
	~snake() {cout << "destroying snake: " << this << "\n";} //non-virtual destructor
};

class pet {
public:
	animal a;
	pet(animal& aa) : a(aa) {cout << "constructing pet: " << this << "\n";} // no zero-arg constructor
	~pet() {cout << "destroying pet: " << this << "\n";}
};

class mypet : public pet {
public:
	//mypet() {cout << "constructing mypet: " << this << "\n";} //compiler error: no matching function for call to 'pet::pet()'
	~mypet() {cout << "destroying mypet: " << this << "\n";}
};

class meat {
public:
	animal a;
	// no constructor
};

class favoritemeat : public meat {
public:
	favoritemeat() {cout << "constructing favoritemeat: " << this << "\n";}
	~favoritemeat() {cout << "destroying favoritemeat: " << this << "\n";}
};

static void justvalues(void) {
	cout << "enter justvalues\n";
	person p1;
	fat f1;
	obese o1;
	animal a1;
	reptile r1;
	cout << "exit justvalues\n\n"; // p1, f1, o1, a1, r1 destroyed correctly after this point
}

static void pointerfun(void) {
	cout << "enter pointerfun\n\n";

	cout << "test 1\n";
	person *p1 = new person();
	person *p2 = new fat(); // person() called automatically
	person *p3 = new obese();
	animal *a1 = new animal();
	animal *a2 = new reptile();
	reptile *r1 = new snake();

	cout << "deleting p1\n";
	delete p1;
	cout << "deleting p2\n";
	delete p2;
	cout << "deleting p3\n";
	delete p3; // ~obese(), ~fat() and ~person called, in order, though ~fat() is not virtual
	cout << "deleting a1\n";
	delete a1;
	cout << "deleting a2\n";
	delete a2; // ~reptile() not called!!
	cout << "deleting r1\n";
	delete r1; //~snake() not called!!
	cout << "end of test 1\n\n";
	//SURPRISE: if an object is deleted using pointer to its base class, its
	// destructor is called if and only if the destructor of the base class or
	// one of its ancestors is declared virtual. if the destructor is not virtual
	// or not provided at all, the destructor of the derived object is not called.

	cout << "test 2\n";
	person *p4 = new fat(string("fatman")); // person() called automatically
	person *p5 = new obese(string("obeseman")); // fat() called automatically
	person *p6 = new obese("obeseman with char"); // fat(const string&) called
	person *p8 = new obese(98.2); // fat() called automatically
	//mypet *m1 = new mypet(); //compiler error: no matching function for call to 'pet::pet()'
	favoritemeat *fm1 = new favoritemeat();
	delete p4;
	delete p5;
	delete p6;
	delete p8;
	//delete m1;
	delete fm1;
	cout << "end of test 2\n\n";
	// LESSONS: no surprise with constructors. constructor of the base class is 
	// always called from that of the derived class. if one is called explicitly
	// from the initialization list, it is called. if none is called explicitly,
	// the default zero-arg constructor of the base is called automatically,
	// if the base class has no constructor or an explicitly defined zero-arg
	// constructor. if base class has one or more constructors and none is a zero-arg
	// constructor, compiler does not provide one on its own and a compiler error is
	// thrown.

	cout << "test 3\n";
	fat f1;
	person *p7 = &f1;
	cout << "end of test 3\n\n";

	cout << "exit pointerfun\n\n"; // f1 destroyed correctly after this point
}

static obese& retvaltest(void) {
	cout << "enter retvaltest\n";
	obese o(39.7);
	cout << "exit retvaltest\n\n";
	return (o); // o destroyed at this point
}

void killstackframe(void) {
	cout << "enter killstackframe\n";
	int garbage[124] = {1234};
	cout << "print garbage: " << garbage[32] << "\n";
	cout << "exit killstackframe\n\n";
}

int main(void) {
	cout << "enter main()\n\n";

	justvalues();
	pointerfun();

	obese& ro = retvaltest();
	killstackframe();
	cout << "obese weight: " << ro.weight << "\n"; // prints wrong value

	obese o = retvaltest();
	killstackframe();
	cout << "obese weight: " << o.weight << "\n"; // prints wrong value

	cout << "exit main()\n\n";
	return (0); // o destroyed at this point
}
