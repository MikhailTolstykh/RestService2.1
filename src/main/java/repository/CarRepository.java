package repository;

import entity.Car;
import entity.Customer;
import entity.Mechanic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarRepository implements CarInterface{

    private String URL = DatabaseConfig.getProperty("db.url");
    private String Username = DatabaseConfig.getProperty("db.username");
    private String Password = DatabaseConfig.getProperty("db.password");

    private static final String INSERT_CAR_SQL = "INSERT INTO cars (model, customer_id) VALUES (?, ?);";
    private static final String SELECT_CAR_BY_ID = "SELECT id, model, customer_id FROM cars WHERE id = ?;";
    private static final String SELECT_ALL_CARS = "SELECT * FROM cars;";
    private static final String DELETE_CAR_SQL = "DELETE FROM cars WHERE id = ?;";
    private static final String UPDATE_CAR_SQL = "UPDATE cars SET model = ?, customer_id = ? WHERE id = ?;";

    private static final String INSERT_CAR_MECHANIC_SQL = "INSERT INTO car_mechanic (car_id, mechanic_id) VALUES (?, ?);";
    private static final String DELETE_CAR_MECHANICS_SQL = "DELETE FROM car_mechanic WHERE car_id = ?;";
    private static final String SELECT_MECHANICS_BY_CAR_ID = "SELECT mechanic_id FROM car_mechanic WHERE car_id = ?;";


    protected Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, Username, Password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

     @Override
    public void addCar(Car car) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CAR_SQL)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setInt(2, car.getCustomerId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Car getCarById(int id) throws SQLException {
        Car car = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CAR_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String model = resultSet.getString("model");
                int customerId = resultSet.getInt("customer_id");

                CustomerRepository customerRepository = new CustomerRepository();
                Customer customer = customerRepository.getCustomerById(customerId);

                MechanicRepository mechanicRepository = new MechanicRepository();
                List<Mechanic> mechanics = mechanicRepository.getMechanicsByCarId(id);

                car = new Car(id, model, customer,customerId);
            }
            return car;
        }
    }

    @Override

    public void updateCar(Car car) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CAR_SQL)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setInt(2, car.getCustomerId());
            preparedStatement.setInt(3, car.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteCar(int id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CAR_SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Car> getAllCars() throws SQLException {
        List<Car> cars = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_CARS)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String model = resultSet.getString("model");
                int customerId = resultSet.getInt("customer_id");

                CustomerRepository customerRepository = new CustomerRepository();
                Customer customer = customerRepository.getCustomerById(customerId);

                MechanicRepository mechanicRepository = new MechanicRepository();
                List<Mechanic> mechanics = mechanicRepository.getMechanicsByCarId(id);

                Car car = new Car(id, model, customer, customerId);
                cars.add(car);
            }
        }

        return cars;
    }


@Override
public List<Mechanic> getMechanicsByCarId(int CarId) throws SQLException {
    List<Mechanic> mechanics = new ArrayList<>();
    try (Connection connection = getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MECHANICS_BY_CAR_ID)) {
        preparedStatement.setInt(1, CarId);
        ResultSet resultSet = preparedStatement.executeQuery();
        MechanicRepository mechanicRepository = new MechanicRepository();
        while (resultSet.next()) {
            int mechanicId = resultSet.getInt("mechanic_id");
            Mechanic mechanic = mechanicRepository.getMechanicById(mechanicId);
            mechanics.add(mechanic);
        }
    }
    return mechanics;
}


}








