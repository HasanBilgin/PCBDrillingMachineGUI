package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.fxml.Initializable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller implements Initializable{

    @FXML
    public TextField txtX,txtY;
    @FXML
    private TableView<Point> tblCoordinate;
    @FXML
    ScatterChart<Number,Number> coordinateChart;
    private TableColumn<Point,Integer> colX;
    private TableColumn<Point,Integer>  colY;
    private XYChart.Series series;
    ObservableList<Point> data;
    @FXML
    public void addAction()      {
        Integer X = 0,Y = 0;
        try {
            String x = txtX.getText();
            String y = txtY.getText();
            X = Integer.parseInt(x);
            Y = Integer.parseInt(y);

        }catch (NumberFormatException e){
            return;
        }
        finally {
            boolean status = false;
            for (int i = 0; i < data.size(); i++)
                if (X.equals(data.get(i).getX())  && Y.equals(data.get(i).getY()))
                    status = true;

            if (!status) {
                data.add(new Point(X, Y));
                tblCoordinate.setItems(data);
                putLocation(data);

            }
        }
    }

    @FXML
    public void deleteAction()  {
        System.out.println("Del");
        Integer X = 0,Y = 0;
        try {
            String x = txtX.getText();
            String y = txtY.getText();
            X = Integer.parseInt(x);
            Y = Integer.parseInt(y);

        }catch (NumberFormatException e){
            return;
        }
        finally {
            for (int i = 0; i < data.size(); i++)
                if (X.equals(data.get(i).getX())  && Y.equals(data.get(i).getY()))
                    data.remove(i);

            tblCoordinate.setItems(data);
            putLocation(data);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colX = new TableColumn("X");
        colX.setCellValueFactory(new PropertyValueFactory<>("X"));

        colY = new TableColumn("Y");
        colY.setCellValueFactory(new PropertyValueFactory<>("Y"));
        tblCoordinate.getColumns().clear();
        tblCoordinate.getColumns().addAll(colX,colY);
        data = FXCollections.observableArrayList();
        series =  new XYChart.Series();
    }

    @FXML
    private void putLocation (ObservableList<Point> data)    {
        series.getData().clear();
        for (Point p : data)
            series.getData().add(new XYChart.Data(p.getX(),p.getY()));
        coordinateChart.getData().clear();
        coordinateChart.getData().add(series);
    }

    @FXML
    private void saveFile ()    {
        try {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)"
                , "*.txt");
        fc.getExtensionFilters().add(extFilter);
        File f = fc.showSaveDialog(new Stage());
        FileWriter fw = new FileWriter(f);
        BufferedWriter buff = new BufferedWriter(fw);
            for (Point p : data) {
                buff.write(p.toString());
                buff.newLine();
            }
            buff.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}