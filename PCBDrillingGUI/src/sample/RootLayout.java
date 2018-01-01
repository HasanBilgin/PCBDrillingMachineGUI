package sample;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.print.Collation;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jssc.SerialPortList;

public class RootLayout extends AnchorPane{

	@FXML SplitPane base_pane;
	@FXML AnchorPane right_pane;
	@FXML VBox left_pane;
	@FXML ComboBox cmbPort;
	@FXML
	public TextField txtX,txtY;
	@FXML
	private TableView<Point> tblCoordinate;
	private TableColumn<Point,Integer> colX;
	private TableColumn<Point,Integer>  colY;
	ObservableList<Point> data;
	private DragIcon mDragOverIcon = null;

	private EventHandler<DragEvent> mIconDragOverRoot = null;
	private EventHandler<DragEvent> mIconDragDropped = null;
	private EventHandler<DragEvent> mIconDragOverRightPane = null;

	public RootLayout() {

		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("RootLayout.fxml")
		);

		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
 			fxmlLoader.load();

		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
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
            }
        }
    }
    private void addActionDragDrop(double x ,double y,DragIconType type) {
        // Component center
        System.out.println();
        Integer X = (int) (x*0.379 ),Y =(int) (y*0.291);
        ArrayList<Point> arr = new ArrayList<Point>();


        //resistor
        if (type.equals(DragIconType.red)){
            Point p = new Point(X-5,Y);
            arr.add(p);
            p = new Point(X+5,Y);
            arr.add(p);
        }
        //diodes
        if (type.equals(DragIconType.green)){
            Point p = new Point(X,Y-3);
            arr.add(p);
            p = new Point(X,Y+3);
            arr.add(p);
        }
        //transistor
        if (type.equals(DragIconType.blue)){
            Point p = new Point(X-2,Y);
            arr.add(p);
            p = new Point(X,Y);
            arr.add(p);
            p = new Point(X+2,Y);
            arr.add(p);
        }
        //capasitor
        if (type.equals(DragIconType.black)){
            Point p = new Point(X-6,Y);
            arr.add(p);
            p = new Point(X+6,Y);
            arr.add(p);
            p = new Point(X,Y-6);
            arr.add(p);
        }
        // ic
        if (type.equals(DragIconType.grey)){
            Point p = new Point(X,Y-2);
            arr.add(p);
            p = new Point(X,Y+2);
            arr.add(p);
            p = new Point(X-2,Y-2);
            arr.add(p);
            p = new Point(X-2,Y+2);
            arr.add(p);
            p = new Point(X-4,Y-2);
            arr.add(p);
            p = new Point(X-4,Y+2);
            arr.add(p);
            p = new Point(X+2,Y-2);
            arr.add(p);
            p = new Point(X+2,Y+2);
            arr.add(p);
            p = new Point(X+4,Y-2);
            arr.add(p);
            p = new Point(X+4,Y+2);
            arr.add(p);
        }
        //led
        if (type.equals(DragIconType.purple)){
            Point p = new Point(X,Y-2);
            arr.add(p);
            p = new Point(X,Y+2);
            arr.add(p);
        }
        //transformer
        if (type.equals(DragIconType.yellow)){
            Point p = new Point(X-8,Y-8);
            arr.add(p);
            p = new Point(X-8,Y+8);
            arr.add(p);
            p = new Point(X+8,Y-8);
            arr.add(p);
            p = new Point(X+8,Y+8);
            arr.add(p);
        }

        for (int i = 0; i < arr.size(); i++) {
            if ( !data.contains(arr.get(i)))
                data.add(arr.get(i));
            tblCoordinate.setItems(data);
        }
    }
	@FXML
	public void deleteAction()  {
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
		}
	}

	@FXML
    public void selectedRow()   {
        Point p = tblCoordinate.getSelectionModel().getSelectedItem();
	    txtX.setText(Integer.toString(p.getX()));
        txtY.setText(Integer.toString(p.getY()));
    }
	@FXML
	private void initialize() {
		colX = new TableColumn("X(mm)");
		colX.setCellValueFactory(new PropertyValueFactory<>("X"));

		colY = new TableColumn("Y(mm)");
		colY.setCellValueFactory(new PropertyValueFactory<>("Y"));
		tblCoordinate.getColumns().clear();
		tblCoordinate.getColumns().addAll(colX,colY);
		data = FXCollections.observableArrayList();

		//Add one icon that will be used for the drag-drop process
		//This is added as a child to the root anchorpane so it can be visible
		//on both sides of the split pane.
		mDragOverIcon = new DragIcon();

		mDragOverIcon.setVisible(false);
		mDragOverIcon.setOpacity(0.65);
		getChildren().add(mDragOverIcon);

		//populate left pane with multiple colored icons for testing
		for (int i = 0; i < 7; i++) {

			DragIcon icn = new DragIcon();

			addDragDetection(icn);

			icn.setType(DragIconType.values()[i]);
			left_pane.getChildren().add(icn);
		}

		buildDragHandlers();
        String[] arr = SerialPortList.getPortNames();
        for (int i = 0; i < arr.length; i++) {
            cmbPort.getItems().add(arr[i]);
        }

	}

	private void addDragDetection(DragIcon dragIcon) {

		dragIcon.setOnDragDetected (new EventHandler <MouseEvent> () {

			@Override
			public void handle(MouseEvent event) {

				// set drag event handlers on their respective objects
				base_pane.setOnDragOver(mIconDragOverRoot);
				right_pane.setOnDragOver(mIconDragOverRightPane);
				right_pane.setOnDragDropped(mIconDragDropped);

				// get a reference to the clicked DragIcon object
				DragIcon icn = (DragIcon) event.getSource();

				//begin drag ops
				mDragOverIcon.setType(icn.getType());
				mDragOverIcon.relocateToPoint(new Point2D (event.getSceneX(), event.getSceneY()));

				ClipboardContent content = new ClipboardContent();
				DragContainer container = new DragContainer();

				container.addData ("type", mDragOverIcon.getType().toString());
				content.put(DragContainer.AddNode, container);

				mDragOverIcon.startDragAndDrop (TransferMode.ANY).setContent(content);
				mDragOverIcon.setVisible(true);
				mDragOverIcon.setMouseTransparent(true);
				event.consume();
			}
		});
	}

	private void buildDragHandlers() {

		//drag over transition to move widget form left pane to right pane
		mIconDragOverRoot = new EventHandler <DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());

				//turn on transfer mode and track in the right-pane's context
				//if (and only if) the mouse cursor falls within the right pane's bounds.
				if (!right_pane.boundsInLocalProperty().get().contains(p)) {

					event.acceptTransferModes(TransferMode.ANY);
					mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
					return;
				}

				event.consume();
			}
		};

		mIconDragOverRightPane = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {

				event.acceptTransferModes(TransferMode.ANY);

				//convert the mouse coordinates to scene coordinates,
				//then convert back to coordinates that are relative to
				//the parent of mDragIcon.  Since mDragIcon is a child of the root
				//pane, coodinates must be in the root pane's coordinate system to work
				//properly.
				mDragOverIcon.relocateToPoint(
						new Point2D(event.getSceneX(), event.getSceneY())
				);
				event.consume();
			}
		};

		mIconDragDropped = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {

				DragContainer container =
						(DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

				container.addData("scene_coords",
						new Point2D(event.getSceneX(), event.getSceneY()));

				ClipboardContent content = new ClipboardContent();
				content.put(DragContainer.AddNode, container);

				event.getDragboard().setContent(content);
				event.setDropCompleted(true);
				addActionDragDrop(mDragOverIcon.getLayoutX(),mDragOverIcon.getLayoutY(),mDragOverIcon.getType());
			}
		};

		this.setOnDragDone (new EventHandler <DragEvent> (){

			@Override
			public void handle (DragEvent event) {

				right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRightPane);
				right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
				base_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);

				mDragOverIcon.setVisible(false);

				DragContainer container =
						(DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

				if (container != null) {
					if (container.getValue("scene_coords") != null) {

						DragIcon droppedIcon = new DragIcon();

						droppedIcon.setType(DragIconType.valueOf(container.getValue("type")));
						right_pane.getChildren().add(droppedIcon);

						Point2D cursorPoint = container.getValue("scene_coords");

						droppedIcon.relocateToPoint(
								new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
						);
					}
				}
				event.consume();
			}
		});
	}


    @FXML
    private void sendArduino()  {
        try {
            data.sort(new Point(0,0));

            if (cmbPort.getSelectionModel().getSelectedItem() != null)  {
                SerialCommArduino s = new SerialCommArduino(cmbPort.getSelectionModel().getSelectedItem().toString());
                FileWriter fw;
                fw = new FileWriter("test.txt");
                BufferedWriter buff = new BufferedWriter(fw);
				buff.write("G21");
				buff.newLine();

				double XForwardCoefficient = 0.055;
                double XBackwardCoefficient = 0.055;
                double YForwardCoefficient = 0.055;
                double YBackwardCoefficient = 0.055;
                for (int i = 0; i < data.size(); i++) {
					if ( (i - 1) < 0 )	{
						double x = XForwardCoefficient * data.get(i).getX();
						double y = YForwardCoefficient * data.get(i).getY();
						buff.write("G91 G0 X" + x + " Y" + y);
						buff.newLine();
					}
					else	{
                        double x = XForwardCoefficient * (data.get(i).getX() - data.get(i-1).getX());
                        double y = YForwardCoefficient * (data.get(i).getY() - data.get(i-1).getY());
                        buff.write("G91 G0 X" + x + " Y" + y);
                        buff.newLine();
					}
                    buff.write("G91 G0 Z1.8");
                    buff.newLine();
                    buff.write("G91 G0 Z-1.8");
                    buff.newLine();

				}
				double x = XForwardCoefficient * (data.get(data.size()-1).getX()) * -1;
				double y = YForwardCoefficient * (data.get(data.size()-1).getY()) * -1;
				buff.write("G91 G0 X" + x + " Y" + y);
				buff.newLine();
                buff.flush();
                buff.close();
                File f = new File("test.txt");
                s.run(f);
				f.delete();
            }
            else    {
                Alert alert = new Alert(Alert.AlertType.NONE,"Port Seçilmedi!!!",ButtonType.OK);
                alert.setTitle("Port Hatası");
                alert.showAndWait();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void clear() {
	    data.clear();
        right_pane.getChildren().clear();
    }
}
