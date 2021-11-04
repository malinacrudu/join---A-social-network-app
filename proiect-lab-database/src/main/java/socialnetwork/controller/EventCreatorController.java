package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import socialnetwork.domain.Event;
import socialnetwork.domain.User;
import socialnetwork.service.CommunityService;
import socialnetwork.utils.StringConverter;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;

public class EventCreatorController {
    public TextField titleField;
    public TextArea descriptionField;
    public TextField locationField;
    public Button pictureButton;
    public DatePicker startDate;
    public Button createButton;
    public DatePicker endDate;
    public ImageView imageView;

    User user;
    private CommunityService service;
    Stage current;
    File chosen;

    public void choose_picture()
    {
        try
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image file","*.png","*.jpg"));
            File file = fileChooser.showOpenDialog(this.current);
            if(file!=null)
            {
                FileInputStream stream = new FileInputStream(file.getAbsolutePath());
                imageView.setImage(new Image(stream));
                chosen=file;
            }
        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(),ButtonType.OK);
            alert.setResizable(true);
            alert.showAndWait();
        }
    }

    public void create_event()
    {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String location = locationField.getText();
        LocalDate startDateValue = startDate.getValue();
        LocalDate endDateValue = endDate.getValue();
        String path = chosen.getAbsolutePath();
        Long creator = user.getId();
        Event ev = new Event(title, description, creator, startDateValue, endDateValue);
        ev.setLocation(location);
        ev.setPath(path);
        try
        {
            service.createEvent(ev);
            titleField.setText("");
            locationField.setText("");
            descriptionField.setText("");
            imageView.setImage(null);
            endDate.setValue(null);
            startDate.setValue(null);
        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(),ButtonType.OK);
            alert.setResizable(true);
            alert.showAndWait();
        }
    }

    public void setService(CommunityService service, User user, Stage current)
    {
        this.service=service;
        this.user = user;
        this.current=current;
        StringConverter myConverter = new StringConverter();
        startDate.setConverter(myConverter);
        endDate.setConverter(myConverter);
        this.endDate.getEditor().setFont(Font.font("Century Gothic", 12));
        this.endDate.getEditor().setAlignment(Pos.CENTER);
        this.startDate.getEditor().setFont(Font.font("Century Gothic", 12));
        this.startDate.getEditor().setAlignment(Pos.CENTER);
    }
}
