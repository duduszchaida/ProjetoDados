package br.edu.ifpr.foz.ifprstore.repositories;

import br.edu.ifpr.foz.ifprstore.connection.ConnectionFactory;
import br.edu.ifpr.foz.ifprstore.exceptions.DatabaseException;
import br.edu.ifpr.foz.ifprstore.exceptions.DatabaseIntegrityException;
import br.edu.ifpr.foz.ifprstore.models.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentRepository {

    Connection connection;
    SellerRepository sellerRepository;
    public DepartmentRepository(){
        connection = ConnectionFactory.getConnection();
    }

    public void delete(Integer id){

        String sql = "DELETE FROM department WHERE Id = ?";

        try {

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            Integer rowsDeleted = statement.executeUpdate();

            if(rowsDeleted > 0){
                System.out.println("Rows deleted: " + rowsDeleted);
            }

        } catch (Exception e){
            throw new DatabaseIntegrityException(e.getMessage());
        }

    }
    public Department insert(Department department) {
        String sql = "INSERT INTO department (Name) VALUES (?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, department.getName());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {

                ResultSet id = statement.getGeneratedKeys();

                id.next();
                Integer departmentId = id.getInt(1);

                System.out.println("Rows inserted: " + rowsInserted);
                System.out.println("Id: " + departmentId);

                department.setId(departmentId);

            }

        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return department;
    }
    public void update(Department department) {
        String sql = "UPDATE department SET Name = ? WHERE Id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, department.getName());
            statement.setInt(2, department.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
    public List<Department> getAll() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM department";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Department department = sellerRepository.instantiateDepartment(resultSet);
                departments.add(department);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }

        return departments;
    }
    public Department getById(Integer id) {
        Department department = null;
        String sql = "SELECT * FROM department WHERE Id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    department = sellerRepository.instantiateDepartment(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }

        return department;
    }
    public List<Department> findByName(String name) {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM department WHERE Name LIKE ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Department department = sellerRepository.instantiateDepartment(resultSet);
                    departments.add(department);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }

        return departments;
    }
    public List<Department> getDepartmentsWithoutSellers() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT d.* FROM department d LEFT JOIN seller s ON d.Id = s.DepartmentId WHERE s.Id IS NULL";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Department department = sellerRepository.instantiateDepartment(resultSet);
                departments.add(department);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }

        return departments;
    }



}
