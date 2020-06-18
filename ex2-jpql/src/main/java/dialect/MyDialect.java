package dialect;

import org.hibernate.dialect.MariaDB10Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MyDialect extends MariaDB10Dialect
{
    public MyDialect()
    {
        registerFunction("myfunction", new StandardSQLFunction("concat", StandardBasicTypes.STRING));
    }
}
