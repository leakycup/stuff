#include <memory>
#include <iostream>
#include <boost/shared_ptr.hpp>

using namespace std;

class organ {
public:
	typedef auto_ptr<organ> autoptr;
	typedef boost::shared_ptr<organ> sharedptr;

	organ() {cout << "constructing organ: " << this << "\n";};
	~organ() {cout << "destroying organ: " << this << "\n";}
};

class hand : public organ {
public:
	typedef auto_ptr<hand> autoptr;
	typedef boost::shared_ptr<hand> sharedptr;

	int fingers;
	hand(int f = 0) : fingers(f) {cout << "constructing hand with " << f << " fingers: " << this << "\n";}
	~hand() {cout << "destroying hand: " << this << "\n";}
};

class leg : public organ {
public:
	leg() {cout << "constructing leg: " << this << "\n";};
	leg(const string *s) {cout << "constructing leg with string: " << this << "\n"; throw 20;} // throw an exception
	~leg() {cout << "destroying leg: " << this << "\n";}
};

class humanoid {
public:
	typedef auto_ptr<humanoid> autoptr;
	typedef boost::shared_ptr<humanoid> sharedptr;

	humanoid() {cout << "constructing humanoid: " << this << "\n";}
	virtual ~humanoid() {cout << "destroying humanoid: " << this << "\n";}
};

class betelguesian : public humanoid {
public:
	typedef auto_ptr<betelguesian> autoptr;

	hand h1; //automatically destroyed, even if there's an exception in initializer list of this class' constructor
	hand *h2; //explicitly destroyed, leaks if there's an exception in initializer list of this class' constructor
	hand *h3; //leaks
	hand::autoptr h4; //automatically destroyed, even if there's an exception in initializer list of this class' constructor. if this member is returned to a caller via a 'get' method, and this object is destroyed, this member survives destruction. if the variable to which returned h4 is stored in caller's code goes out of scope, h4 is destroyed though betelguesian may not have been destroyed yet. this can be disastraous.
	hand::sharedptr h5; //automatically destroyed, even if there's an exception in initializer list of this class' constructor. if this member is returned to a caller via a 'get' method, and this object is destroyed, this member survives destruction.
	leg l; //automatically destroyed

	betelguesian() : h2(new hand(2)), h3(new hand(3)), h4(new hand(4)), h5(new hand(5)) {cout << "constructing betelguesian: " << this << "\n";}
	betelguesian(const string *s) : l(s), h2(new hand(2)), h3(new hand(3)), h4(new hand(4)), h5(new hand(5)) {cout << "constructing betelguesian: " << this << "\n";}
	~betelguesian() {delete h2; cout << "destroying betelguesian: " << this << "\n";}

	hand gimmeahand_1(void) {return h1;}
	hand *gimmeahand_2(void) {return h2;}
	hand *gimmeahand_3(void) {return h3;}
	hand::autoptr gimmeahand_4(void) {return h4;}
	hand::sharedptr gimmeahand_5(void) {return h5;}
};

static humanoid& gethumanoid_1(void) {
	cout << "enter gethumanoid_1\n";
	humanoid h; //constructed here, automatically destroyed when this function returns
	cout << "exit gethumanoid_1\n\n";
	return (h);
}

static humanoid::autoptr gethumanoid_2(void) {
	cout << "enter gethumanoid_2\n";
	humanoid::autoptr h(new humanoid()); //constructed here, automatically destroyed when main returns
	cout << "exit gethumanoid_2\n\n";
	return (h);
}

static humanoid* gethumanoid_3(void) {
	cout << "enter gethumanoid_3\n";
	humanoid *h = new humanoid(); //constructed here, never destroyed automatically
	cout << "exit gethumanoid_3\n\n";
	return (h);
}

static humanoid::autoptr& gethumanoid_4(void) {
	cout << "enter gethumanoid_4\n";
	humanoid::autoptr h(new humanoid()); //constructed here, segfault when an attempt is made to destroy it at the exit of main
	cout << "exit gethumanoid_4\n\n";
	return (h);
}

static humanoid::sharedptr gethumanoid_5(void) {
	cout << "enter gethumanoid_5\n";
	humanoid::sharedptr h(new humanoid()); //constructed here, automatically destroyed when main returns
	cout << "exit gethumanoid_5\n\n";
	return (h);
}

static void whackabetelguesian(void) {
	cout << "enter whackabetelguesian\n";
	betelguesian b;
	cout << "exit whackabetelguesian\n\n";
}

static void whackabetelguesian_again(void) {
	cout << "enter whackabetelguesian_again\n";
	betelguesian b(NULL); //should cause an exception in intializer list of the constructor
	cout << "exit whackabetelguesian_again\n\n";
}

int main(void) {
	cout << "enter main\n";

	humanoid& h1 = gethumanoid_1();
	humanoid::autoptr h2 = gethumanoid_2();
	humanoid *h3 = gethumanoid_3();
	//humanoid::autoptr h4 = gethumanoid_4(); // causes segfault when main() returns
	humanoid::sharedptr h5 = gethumanoid_5();

	cout << "enter get method test 1\n";
	betelguesian *b = new betelguesian();
	hand hd1 = b->gimmeahand_1();
	hand *hd2 = b->gimmeahand_2();
	hand *hd3 = b->gimmeahand_3();
	//organ::autoptr hd4 = b->gimmeahand_4(); //syntax error: how to support inheritence with templates?
	hand::autoptr hd5 = b->gimmeahand_4();
	hand::sharedptr hd6 = b->gimmeahand_5();
	organ::sharedptr hd7 = b->gimmeahand_5(); //ok
	delete b;
	cout << "exit get method test 1\n\n";

	cout << "enter get method test 2\n";
	betelguesian *c = new betelguesian();
	do {
		hand::autoptr hd6 = c->gimmeahand_4(); //hd6 gets the address of c->h4
		hand::sharedptr hd7 = c->gimmeahand_5(); //hd7 gets the address of c->h5
		//auto_ptr, hd6, and shared_ptr, hd7, go out of scope and the containing object, c, does not. however, c->h4 is destroyed at this point and c->h5 is not.
	} while (0);
	do {
		hand::autoptr hd8 = c->gimmeahand_4(); //access c->h4 again
		//hd8->fingers = 6; //seg fault
		hand::sharedptr hd9 = c->gimmeahand_5(); //access c->h5 again
		hd9->fingers = 6; //ok
	} while (0);
	delete c; // c->h4 is not destroyed again, c->h5 is destroyed at this point.
	cout << "exit get method test 2\n\n";

	whackabetelguesian();
	try {
		whackabetelguesian_again();
	} catch (int e) {
		cout << "caught exception: " << e << "\n";
	}

	cout << "exit main\n";
	return (0);
}
