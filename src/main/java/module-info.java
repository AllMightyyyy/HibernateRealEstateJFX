module org.zakaria.realestatehibernatefx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.desktop;
    requires static lombok;
    requires org.slf4j;
    requires jakarta.validation;

    opens org.zakaria.realestatehibernatefx to javafx.fxml, org.hibernate.orm.core;
    exports org.zakaria.realestatehibernatefx;
    exports org.zakaria.realestatehibernatefx.controller;
    opens org.zakaria.realestatehibernatefx.controller to javafx.fxml, org.hibernate.orm.core;
    exports org.zakaria.realestatehibernatefx.utility;
    opens org.zakaria.realestatehibernatefx.utility to javafx.fxml, org.hibernate.orm.core;
    exports org.zakaria.realestatehibernatefx.model;
    opens org.zakaria.realestatehibernatefx.model to javafx.fxml, org.hibernate.orm.core;
    exports org.zakaria.realestatehibernatefx.repositories;
    opens org.zakaria.realestatehibernatefx.repositories to javafx.fxml, org.hibernate.orm.core;
}