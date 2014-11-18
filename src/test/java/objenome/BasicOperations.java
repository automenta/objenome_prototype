package objenome;

import java.util.Date;


public class BasicOperations {

    public static void main(String[] args) {

        case1();
        case2();
        case3();
        case4();
        case5();
        case6();
        case7();
        case8();
        case9();
        case10();
        case11();
    }

    public static class Connection {

    }

    public static interface UserDAO {

        public String getUsername(int id);
    }

    public static class JdbcUserDAO implements UserDAO {

        private Connection conn;

        public void setConnection(Connection conn) {
            this.conn = conn;
        }

        @Override
        public String getUsername(int id) {

            // connection will be injected by the container...
            if (conn == null) {
                throw new IllegalStateException("conn is null!");
            }

            // usable the connection to get the username...
            return "saoj";
        }
    }

    public static interface AccountDAO {

        public double getBalance(int id);
    }

    public static class JdbcAccountDAO implements AccountDAO {

        private final Connection conn;

        public JdbcAccountDAO(Connection conn) {
            this.conn = conn;
        }

        @Override
        public double getBalance(int id) {

            assert conn != null;

            // usable the connection to get the balance...
            return 1000000D;
        }
    }

    private static void case9() {

        Context c = new DefaultContext();

        c.use("connection", Connection.class); // in real life this will be a connection pool factory...
        // all beans that need a connection in the constructor or setter will receive one...
        
        c.usable("accountDAO", JdbcAccountDAO.class);
        c.usable("userDAO", JdbcUserDAO.class);

        AccountDAO accountDAO = c.get("accountDAO");
        UserDAO userDAO = c.get("userDAO");

        System.out.println(accountDAO.getBalance(25)); // => 1000000
        System.out.println(userDAO.getUsername(45)); // => "saoj"
    }

    private static void case1() {

        Context c = new DefaultContext();

        c.usable("myString1", String.class);

        String myString1 = c.get("myString1");

        System.out.println(myString1); // ==> "" ==> default constructor new String() was used

        c.usable("myString2", String.class).addInitValue("saoj");

        String myString2 = c.get("myString2");

        System.out.println(myString2); // ==> "saoj" ==> constructor new String("saoj") was used

        c.usable("myDate1", Date.class).addPropertyValue("hours", 15) // setHours(15)
                .addPropertyValue("minutes", 10) // setMinutes(10)
                .addPropertyValue("seconds", 45); // setSeconds(45)

        Date myDate1 = c.get("myDate1");

        System.out.println(myDate1); // ==> a date with time 15:10:45
    }

    private static void case5() {

        Context c = new DefaultContext();

        c.usable("connection", Connection.class); // in real life this will be a connection pool factory...

        c.usable("accountDAO", JdbcAccountDAO.class).constructorUse("connection");

        AccountDAO accountDAO = c.get("accountDAO");

        System.out.println(accountDAO.getBalance(25)); // => 1000000
    }

    private static void case7() {

        Context c = new DefaultContext();

        c.use("connection", Connection.class); // in real life this will be a connection pool factory...
        // all beans that need a connection in the constructor will get one...

        c.usable("accountDAO", JdbcAccountDAO.class);

        AccountDAO accountDAO = c.get("accountDAO");

        System.out.println(accountDAO.getBalance(25)); // => 1000000

    }

    private static void case6() {

        Context c = new DefaultContext();

        c.usable("connection", Connection.class); // in real life this will be a connection pool factory...

        c.usable("userDAO", JdbcUserDAO.class).addPropertyDependency("connection");

        UserDAO userDAO = c.get("userDAO");

        System.out.println(userDAO.getUsername(54)); // => "saoj"
    }

    private static void case8() {

        Context c = new DefaultContext();

        c.use("connection", Connection.class); // in real life this will be a connection pool factory...

        c.usable("userDAO", JdbcUserDAO.class);
        

        UserDAO userDAO = c.get("userDAO");

        System.out.println(userDAO.getUsername(54)); // => "saoj"

    }

    private static void case2() {

        Context c = new DefaultContext();

        c.usable("myString", Scope.SINGLETON, String.class).addInitValue("saoj");

        String s1 = c.get("myString");

        String s2 = c.get("myString");

        System.out.println(s1 == s2); // ==> true ==> same instance

        System.out.println(s1.equals(s2)); // ==> true => of course
    }

    private static void case3() {

        Context c = new DefaultContext();

        c.usable("userDAO", JdbcUserDAO.class);

        c.use("connection", Connection.class); // in real life this would be a connection pool
        // or the hibernate SessionFactory

        // "conn" = the name of the property
        // Connection.class = the type of the property
        // "connection" = the source from where the dependency will come from
        

        UserDAO userDAO = c.get("userDAO");

        // the container detects that userDAO has a dependency: name = "conn" and type = "Connection.class"
        // where does it go to get the dependency to insert?
        // In itself: it does a Context.get("connection") => "connection" => the source
        System.out.println(userDAO.getUsername(11)); // ==> "saoj" ==> connection is not null as expected...
    }

    public static class SomeService {

        private UserDAO userDAO;

        public void setUserDAO(UserDAO userDAO) {
            this.userDAO = userDAO;
        }

        public void doSomething() {
            System.out.println(userDAO.getUsername(11));
        }
    }

    private static void case4() {

        Context c = new DefaultContext();

        c.usable("userDAO", JdbcUserDAO.class);

        c.use("connection", Connection.class);

        SomeService service = new SomeService();

        // populate (apply) all properties of SomeService with
        // beans from the container
        c.apply(service).doSomething(); 
    }

    public static class ExampleService {

        private final UserDAO userDAO;

        public ExampleService(UserDAO userDAO) {
            this.userDAO = userDAO;
        }

        public void doSomething() {
            System.out.println(userDAO.getUsername(11));
        }
    }
    
    public static class ParameterX {
        public ParameterX() { }        
    }
    
    public static class ExampleService2 {

        private final UserDAO userDAO;
        private final ParameterX x;

        public ExampleService2(UserDAO userDAO, ParameterX x) {
            this.userDAO = userDAO;
            this.x = x;
        }

        public void function() {
            System.out.println(userDAO.getUsername(11) + " " + x);
        }
    }    

    private static void case10() {

        Context c = new DefaultContext();

        c.usable("userDAO", JdbcUserDAO.class);        

        c.use("connection", Connection.class);

        ExampleService service = c.get(ExampleService.class);

        service.doSomething(); // ==> "saoj"
    }

    private static void case11() {

        Context c = new DefaultContext();

        c.usable(JdbcUserDAO.class);        
        c.usable(ParameterX.class);
        
        c.use("connection", Connection.class); //wires to setter

        ExampleService2 service = c.get(ExampleService2.class);

        service.function(); // ==> "saoj"
    }
    
}
