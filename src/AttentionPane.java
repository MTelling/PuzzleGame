import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AttentionPane extends StackPane {
	
	String text;
	double fontSize;
	boolean closeable;
	
	public AttentionPane(String text, String id, double fontSize, double shadowSize, boolean closeable) {
		super();
		this.setId(id);
		
		this.setStyle("-fx-background-color: rgba(100, 100, 100, 0.8); -fx-background-radius: 10;");
		this.setPrefSize(700, 700);
		this.setLayoutX(40);
		this.setLayoutY(66);
		this.closeable = closeable;
		
		Label label = new Label(text);
		label.setStyle("-fx-text-fill: whitesmoke; -fx-font-style: italic; -fx-font-weight: bold; -fx-padding: 0 0 20 0;");
		label.setFont(new Font(fontSize));
		DropShadow labelShadow = new DropShadow(shadowSize, Color.WHITESMOKE);
		label.setEffect(labelShadow);
		this.getChildren().add(label);
		
		
		//If the closable option is true, this should be removed when clicked. 
		if (this.closeable){ 
			this.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					((Group) AttentionPane.this.getParent()).getChildren().remove(AttentionPane.this);

					
				}
				
			});
		}
	}
	
}