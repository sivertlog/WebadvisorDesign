import java.util.HashMap;

public class TestUsers {
    private HashMap<String, String> users = new HashMap<String, String>();

    public TestUsers() {
        this.users.put("Sivert", "1234");
        this.users.put("John", "Doe");
    }

    public boolean checkPassword(String name, String password){
        return users.get(name) == password;
    }

    public void addUser(String name, String password) {
        if (users.containsKey(name)) {
        } else {
            users.put(name, password);
        }
    }
}
