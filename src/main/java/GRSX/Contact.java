package GRSX;

public class Contact {
    private String name;
    private String address;

    public Contact(String name, String address)
    {
        this.name = name;
        this.address = address;
    }

    public String getName()
    {
        return this.name;
    }

    public String getAddress()
    {
        return this.address;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }
}
