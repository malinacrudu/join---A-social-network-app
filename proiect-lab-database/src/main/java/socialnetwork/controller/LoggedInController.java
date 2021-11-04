package socialnetwork.controller;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import socialnetwork.domain.User;
import socialnetwork.service.CommunityService;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import socialnetwork.utils.Observer;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;


public class LoggedInController implements Observer {

    public Tab myEventsTab;
    public Tab allEventsTab;
    public Label labelFullName;
    public Label labelFriends;
    public Label labelUsername;
    public ImageView img;
    public SplitPane splitPane;

    Stage stage;
    Stage current;
    User user;
    private CommunityService service;
    File chosen;
    @FXML
    Button LogOutButton;
    @FXML
    Button deleteAccountButton;
    @FXML
    Tab friendsTab;
    @FXML
    Tab composeTab;
    @FXML
    Tab newFriendsTab;
    @FXML
    Tab requestsTab;
    @FXML
    Tab inboxTab;
    @FXML
    Tab reportsTab;
    @FXML
    TabPane tPane;
    @FXML
    Tab eventCreatorTab;

    public void initConnections()
    {
        try
        {
            File profile1 = new File("src/person.png");
            Image profile_image1 = new Image(profile1.toURI().toString());
            if(user.getPath()!=null && !user.getPath().equals(""))
            {
                File profile = new File(user.getPath());
                Image profile_image = new Image(profile.toURI().toString());
                img.setImage(profile_image);

                if(!profile.exists())
                    img.setImage(profile_image1);
            }
            else
                img.setImage(profile_image1);
            user = service.findUser(user);
            labelUsername.setText(user.getUsername());
            labelUsername.setFont(Font.font("Century Gothic", 13));
            labelFullName.setFont(Font.font("Century Gothic", 13));
            labelFriends.setFont(Font.font("Century Gothic", 13));
            labelFriends.setTextFill(Paint.valueOf("#8F4F40"));
            labelUsername.setTextFill(Paint.valueOf("#8F4F40"));
            labelFullName.setTextFill(Paint.valueOf("#8F4F40"));
            labelFullName.setText(user.getFirstName()+" "+user.getLastName());
            labelFriends.setText("Number of friends: "+user.getFriends().size());

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/addfriend.fxml"));
            BorderPane root = loader.load();
            Stage addStage = new Stage();
            addStage.initStyle(StageStyle.DECORATED);
            AddFriendController addFriendController = loader.getController();
            addStage.setScene(new Scene(root, 300, 300, Color.TRANSPARENT));
            addStage.setTitle("Add friends");
            addFriendController.setService(service, user);

            newFriendsTab.setContent(addStage.getScene().getRoot());
            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/compose.fxml"));
            AnchorPane root2 = loader.load();
            Stage composeStage = new Stage();
            composeStage.initStyle(StageStyle.DECORATED);
            ComposeMessageController composeMessageController= loader.getController();
            composeStage.setScene(new Scene(root2, 800, 500, Color.TRANSPARENT));
            composeMessageController.setService(service, user);
            composeTab.setContent(composeStage.getScene().getRoot());

            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/all_events.fxml"));
            root2 = loader.load();
            Stage allEventsStage = new Stage();
            allEventsStage.initStyle(StageStyle.DECORATED);
            AllEventsController allEventsController= loader.getController();
            allEventsStage.setScene(new Scene(root2, 800, 500, Color.TRANSPARENT));
            allEventsController.setService(service, user);
            allEventsTab.setContent(allEventsStage.getScene().getRoot());



            File file5 = new File("src/add-friend.png");
            Image image5 = new Image(file5.toURI().toString());
            ImageView ivf = new ImageView();
            ivf.setImage(image5);
            ivf.setFitHeight(25);
            ivf.setFitWidth(25);
            newFriendsTab.setGraphic(ivf);

            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/friends.fxml"));
            root = loader.load();
            Stage friendsStage = new Stage();
            friendsStage.initStyle(StageStyle.DECORATED);
            FriendsController friendsController = loader.getController();
            friendsStage.setScene(new Scene(root, 300, 300, Color.TRANSPARENT));
            friendsStage.setTitle("Your friends");
            friendsController.setService(service, user);
            friendsTab.setContent(friendsStage.getScene().getRoot());

            File file6 = new File("src/friends.png");
            Image image6 = new Image(file6.toURI().toString());
            ImageView ivfr = new ImageView();
            ivfr.setImage(image6);
            ivfr.setFitHeight(25);
            ivfr.setFitWidth(25);
            friendsTab.setGraphic(ivfr);

            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/request.fxml"));
            root = loader.load();
            Stage requestStage = new Stage();
            requestStage.initStyle(StageStyle.DECORATED);
            RequestController requestController= loader.getController();
            requestStage.setScene(new Scene(root, 800, 500, Color.TRANSPARENT));
            requestStage.setTitle("See friend requests");
            requestController.setService(service, user);
            requestsTab.setContent(requestStage.getScene().getRoot());


            File file = new File("src/msg.png");
            Image image = new Image(file.toURI().toString());
            ImageView ivm = new ImageView();
            ivm.setImage(image);
            ivm.setFitHeight(25);
            ivm.setFitWidth(25);
            inboxTab.setGraphic(ivm);

            File file1 = new File("src/convo.png");
            Image image1 = new Image(file1.toURI().toString());
            ImageView ivc = new ImageView();
            ivc.setImage(image1);
            ivc.setFitHeight(25);
            ivc.setFitWidth(25);
            composeTab.setGraphic(ivc);

            File file2 = new File("src/raport.png");
            Image image2 = new Image(file2.toURI().toString());
            ImageView ivr = new ImageView();
            ivr.setImage(image2);
            ivr.setFitHeight(25);
            ivr.setFitWidth(25);
            reportsTab.setGraphic(ivr);

            File file3 = new File("src/friend-request.png");
            Image image3 = new Image(file3.toURI().toString());
            ImageView iva = new ImageView();
            iva.setImage(image3);
            iva.setFitHeight(30);
            iva.setFitWidth(30);
            requestsTab.setGraphic(iva);

            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/inbox.fxml"));
            root = loader.load();
            Stage inboxStage = new Stage();
            inboxStage.initStyle(StageStyle.DECORATED);
            InboxController inboxController= loader.getController();
            inboxStage.setScene(new Scene(root, 800, 500, Color.TRANSPARENT));
            inboxStage.setTitle("See friend requests");
            inboxController.setService(service, user);
            inboxTab.setContent(inboxStage.getScene().getRoot());



            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/reports.fxml"));
            root = loader.load();
            Stage reportsStage = new Stage();
            reportsStage.initStyle(StageStyle.DECORATED);
            ReportsController reportsController= loader.getController();
            reportsStage.setScene(new Scene(root, 800, 500, Color.TRANSPARENT));
            reportsController.setService(service, user);
            reportsTab.setContent(reportsStage.getScene().getRoot());

            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/event_creator.fxml"));
            root = loader.load();
            Stage eventCreatorStage = new Stage();
            eventCreatorStage.initStyle(StageStyle.DECORATED);
            EventCreatorController eventCreatorController= loader.getController();
            eventCreatorStage.setScene(new Scene(root, 800, 500, Color.TRANSPARENT));
            eventCreatorController.setService(service, user,eventCreatorStage);
            eventCreatorTab.setContent(eventCreatorStage.getScene().getRoot());

            File file4 = new File("src/add-event.png");
            Image image4 = new Image(file4.toURI().toString());
            ImageView ive = new ImageView();
            ive.setImage(image4);
            ive.setFitHeight(25);
            ive.setFitWidth(30);
            eventCreatorTab.setGraphic(ive);

            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/my_events.fxml"));
            root2 = loader.load();
            Stage myEventsStage = new Stage();
            myEventsStage.initStyle(StageStyle.DECORATED);
            MyEventsController myEventsController= loader.getController();
            myEventsStage.setScene(new Scene(root2, 800, 500, Color.TRANSPARENT));
            myEventsController.setService(service, user);
            myEventsTab.setContent(myEventsStage.getScene().getRoot());

            File file7 = new File("src/my_calendar.png");
            Image image7 = new Image(file7.toURI().toString());
            ImageView ivev = new ImageView();
            ivev.setImage(image7);
            ivev.setFitHeight(25);
            ivev.setFitWidth(30);
            myEventsTab.setGraphic(ivev);

            File file8 = new File("src/calendar.png");
            Image image8 = new Image(file8.toURI().toString());
            ImageView iveva = new ImageView();
            iveva.setImage(image8);
            iveva.setFitHeight(25);
            iveva.setFitWidth(30);
            allEventsTab.setGraphic(iveva);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void setService(CommunityService service, Stage stage, Stage current, User user)
    {
        this.service=service;
        this.stage=stage;
        this.current =current;
        this.user = user;
        service.add_observer(this);
        initConnections();

    }

    public void log_out()
    {
        this.stage.show();
        this.current.close();
        service.remove_observers();
    }


    public void delete_account(ActionEvent actionEvent)
    {
        try
        {
            service.deleteUser(user);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Your account is gone!", ButtonType.OK);
            alert.setResizable(true);
            alert.showAndWait();
            this.current.close();
            this.stage.show();

        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.setResizable(true);
            alert.show();
        }
    }


    public void change_picture(ActionEvent actionEvent)
    {
        try
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image file","*.png","*.jpg"));
            File file = fileChooser.showOpenDialog(this.current);
            if(file!=null)
            {
                FileInputStream stream = new FileInputStream(file.getAbsolutePath());
                img.setImage(new Image(stream));
                chosen=file;
                service.choose_picture(user,chosen.getAbsolutePath());

            }
        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(),ButtonType.OK);
            alert.setResizable(true);
            alert.showAndWait();
        }
    }


    public void notification(Event event)
    {

        ArrayList<socialnetwork.domain.Event> myEvents = service.getMyEvents(user);
        long minimumDays = (long) 9999999;
        socialnetwork.domain.Event minimum = null;
        if(myEvents.size()!=0)
        {

            for(socialnetwork.domain.Event event1 : myEvents)
            {
                long days = Period.between(LocalDate.now(),event1.getStart()).getDays();
                if(days>=0 && days<minimumDays)
                {
                    minimum=event1;
                    minimumDays=days;
                }
            }

        }
        String anunt="";
        if(minimum==null)anunt="You do not have any upcoming events!";
            else if(minimumDays==0)
                anunt="The event "+minimum.getTitle()+" is today!";
            else
                anunt="In "+minimumDays+" days you will be attending "+minimum.getTitle()+"!";
        Alert alert = new Alert(Alert.AlertType.INFORMATION,anunt,ButtonType.OK);
        alert.setResizable(true);
        alert.showAndWait();

    }

    @Override
    public void execute_update()
    {
        user = service.findUser(user);
        labelFriends.setText("Number of friends: "+user.getFriends().size());
    }
}
