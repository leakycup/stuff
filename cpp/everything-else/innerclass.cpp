class outer
{
    public:
	//privtae:
    int member1;
    class inner
    {
        int member1;
    };
};

outer::inner object;

