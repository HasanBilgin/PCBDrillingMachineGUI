package sample;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class RootLayout extends AnchorPane{

	@FXML SplitPane base_pane;
	@FXML AnchorPane right_pane;
	@FXML VBox left_pane;
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
				putLocation(data);

			}
		}
	}
    private void addActionDragDrop(double x ,double y) {
        Integer X = (int) x,Y =(int) y;
        X = (X-75) / 2;
        Y /= 2;
        boolean status = false;
        for (int i = 0; i < data.size(); i++)
            if (X.equals(data.get(i).getX())  && Y.equals(data.get(i).getY()))
                status = true;

        if (!status) {
            data.add(new Point(X, Y));
            tblCoordinate.setItems(data);
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
	@FXML
	private void putLocation (ObservableList<Point> data)    {
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

	@FXML
	private void loadFile() {
		try {
			FileChooser fc = new FileChooser();
			fc.setTitle("Open File");
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)"
					, "*.txt");
			fc.getExtensionFilters().add(extFilter);
			File f = fc.showOpenDialog(new Stage());
			Scanner scan = new Scanner(f);
			int x,y;
			data.clear();
			while (scan.hasNextInt()) {
				x = scan.nextInt();
				y = scan.nextInt();
				data.add(new Point(x,y));
			}

			putLocation(data);
			tblCoordinate.setItems(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	@FXML
	private void sendArduino()  {
	}
	@FXML
	private void initialize() {
		colX = new TableColumn("X");
		colX.setCellValueFactory(new PropertyValueFactory<>("X"));

		colY = new TableColumn("Y");
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
				addActionDragDrop(mDragOverIcon.getLayoutX(),mDragOverIcon.getLayoutY());
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
}
