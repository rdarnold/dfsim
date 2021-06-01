package dfsim;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "people")
public class PersonListWrapper {

    private List<Person> list;

    @XmlElement(name = "person")
    public List<Person> getPersonList() {
        return list;
    }

    public void setPersonList(List<Person> newList) {
        this.list = newList;
    }
}