# EBF-Code-sample

## Employee Information ODATA API

Employee Information API is developed to maintain the Information related to several companies' Employees via oData Restful API. This project is developed based on maven and Olingo JPA version 2. 

## Build and Run

It is a maven Spring-Boot project with Java 8, so we need to just run the following command to build and run the project. 

First clone [the project](https://github.com/mjza/EBF-Code-sample) in a folder of your choice (in the rest of this manual, I assumed you will copy it in a folder with name `EBF-Code-sample`). 

```
git@github.com:mjza/EBF-Code-sample.git
```

Then go to the project folder and run the following command to install required maven libraries:

```
cd EBF-Code-sample
mvn clean install
```
 
Go to `EBF-Code-sample\target` and run the following command:

```
java -jar ebf-code-sample-odata-0.1.0.jar
```

Above step will start the Tomcat container on default port 8080. Now we can access the application using the following URL in the browser and we can test the oData API.

```
http://localhost:8080/
```

You can even run the app in a terminal by typing the following command inside the project folder:

```
mvn spring-boot:run
```

![Screen](./diagrams/Snapshut.gif)

Below will be the metadata URL:

```
http://localhost:8080/odata.svc/$metadata
```

You must be able to see an output like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<edmx:Edmx xmlns:edmx="http://schemas.microsoft.com/ado/2007/06/edmx" Version="1.0">
   <edmx:DataServices xmlns:m="http://schemas.microsoft.com/ado/2007/08/dataservices/metadata" m:DataServiceVersion="1.0">
      <Schema xmlns="http://schemas.microsoft.com/ado/2008/09/edm" Namespace="com.mjzsoft">
         <EntityType Name="Employee">
            <Key>
               <PropertyRef Name="Id" />
            </Key>
            <Property xmlns:sap="http://www.sap.com/Protocols/SAPData" Name="Address" Type="Edm.String" Nullable="true" MaxLength="255" sap:label="Address" sap:deletable="false" sap:filterable="true" sap:updatable="true" sap:sortable="false" sap:creatable="true" />
            <Property xmlns:sap="http://www.sap.com/Protocols/SAPData" Name="CompanyId" Type="Edm.String" Nullable="false" MaxLength="255" sap:label="Company ID" />
            <Property xmlns:sap="http://www.sap.com/Protocols/SAPData" Name="Email" Type="Edm.String" Nullable="true" MaxLength="255" sap:label="Email" sap:deletable="false" sap:filterable="true" sap:updatable="true" sap:sortable="true" sap:creatable="true" />
            <Property xmlns:sap="http://www.sap.com/Protocols/SAPData" Name="Firstname" Type="Edm.String" Nullable="false" MaxLength="255" sap:label="Name" sap:deletable="false" sap:filterable="true" sap:updatable="true" sap:sortable="true" sap:creatable="true" />
            <Property xmlns:sap="http://www.sap.com/Protocols/SAPData" Name="Id" Type="Edm.Int32" Nullable="false" sap:label="Employee ID" sap:deletable="false" sap:filterable="true" sap:updatable="false" sap:sortable="true" sap:creatable="false" />
            <Property xmlns:sap="http://www.sap.com/Protocols/SAPData" Name="Lastname" Type="Edm.String" Nullable="false" MaxLength="255" sap:label="Surname" sap:deletable="false" sap:filterable="true" sap:updatable="true" sap:sortable="true" sap:creatable="true" />
            <Property xmlns:sap="http://www.sap.com/Protocols/SAPData" Name="Salary" Type="Edm.Double" Nullable="false" sap:label="Salary" sap:deletable="false" sap:filterable="true" sap:updatable="true" sap:sortable="true" sap:creatable="true" />
            <NavigationProperty Name="Company" Relationship="com.mjzsoft.Employee_Company_Many_ZeroToOne0" FromRole="Employee" ToRole="Company" />
         </EntityType>
         <EntityType Name="Company">
            <Key>
               <PropertyRef Name="Id" />
            </Key>
            <Property xmlns:sap="http://www.sap.com/Protocols/SAPData" Name="AverageSalary" Type="Edm.Double" Nullable="false" sap:label="Average Salary" sap:deletable="false" sap:filterable="true" sap:updatable="false" sap:sortable="true" sap:creatable="false" />
            <Property xmlns:sap="http://www.sap.com/Protocols/SAPData" Name="Id" Type="Edm.String" Nullable="false" MaxLength="255" sap:label="Company ID" sap:deletable="false" sap:filterable="true" sap:updatable="false" sap:sortable="true" sap:creatable="true" />
            <Property xmlns:sap="http://www.sap.com/Protocols/SAPData" Name="Name" Type="Edm.String" Nullable="false" MaxLength="255" sap:label="Company Name" sap:deletable="false" sap:filterable="true" sap:updatable="true" sap:sortable="true" sap:creatable="true" />
            <NavigationProperty Name="Employees" Relationship="com.mjzsoft.Employee_Company_Many_ZeroToOne0" FromRole="Company" ToRole="Employee" />
         </EntityType>
         <Association Name="Employee_Company_Many_ZeroToOne0">
            <End Type="com.mjzsoft.Employee" Multiplicity="*" Role="Employee" />
            <End Type="com.mjzsoft.Company" Multiplicity="0..1" Role="Company" />
            <ReferentialConstraint>
               <Principal Role="Company">
                  <PropertyRef Name="Id" />
               </Principal>
               <Dependent Role="Employee">
                  <PropertyRef Name="CompanyId" />
               </Dependent>
            </ReferentialConstraint>
         </Association>
         <EntityContainer Name="com.mjzsoft" m:IsDefaultEntityContainer="true">
            <EntitySet Name="Employees" EntityType="com.mjzsoft.Employee" />
            <EntitySet Name="Companies" EntityType="com.mjzsoft.Company" />
            <AssociationSet Name="Employee_Company_Many_ZeroToOne0Set" Association="com.mjzsoft.Employee_Company_Many_ZeroToOne0">
               <End EntitySet="Employees" Role="Employee" />
               <End EntitySet="Companies" Role="Company" />
            </AssociationSet>
         </EntityContainer>
         <Annotations xmlns="http://docs.oasis-open.org/odata/ns/edm" Target="com.mjzsoft.Employee">
            <Annotation Term="com.sap.vocabularies.UI.v1.LineItem">
               <Collection>
                  <Record Type="com.sap.vocabularies.UI.v1.DataField">
                     <PropertyValue Path="Firstname" Property="Value" />
                  </Record>
                  <Record Type="com.sap.vocabularies.UI.v1.DataField">
                     <PropertyValue Path="Lastname" Property="Value" />
                  </Record>
                  <Record Type="com.sap.vocabularies.UI.v1.DataField">
                     <PropertyValue Path="Email" Property="Value" />
                  </Record>
                  <Record Type="com.sap.vocabularies.UI.v1.DataField">
                     <PropertyValue Path="Address" Property="Value" />
                  </Record>
                  <Record Type="com.sap.vocabularies.UI.v1.DataField">
                     <PropertyValue Path="Salary" Property="Value" />
                  </Record>
               </Collection>
            </Annotation>
         </Annotations>
         <Annotations xmlns="http://docs.oasis-open.org/odata/ns/edm" Target="com.mjzsoft.Company">
            <Annotation Term="com.sap.vocabularies.UI.v1.LineItem">
               <Collection>
                  <Record Type="com.sap.vocabularies.UI.v1.DataField">
                     <PropertyValue Path="Id" Property="Value" />
                  </Record>
                  <Record Type="com.sap.vocabularies.UI.v1.DataField">
                     <PropertyValue Path="Name" Property="Value" />
                  </Record>
                  <Record Type="com.sap.vocabularies.UI.v1.DataField">
                     <PropertyValue Path="AverageSalary" Property="Value" />
                  </Record>
               </Collection>
            </Annotation>
         </Annotations>
      </Schema>
   </edmx:DataServices>
</edmx:Edmx>
```

## Why did I use oData standard and not a tailor made API?

Because I had no time to make a front-end application. Some years ago I made this front-end module that can generate the views based on oData metadata automatically. Therefore, we can add a new table in our back-end and we will see that table easily in the list in the front-end. However, using oData has much more benefits.  

