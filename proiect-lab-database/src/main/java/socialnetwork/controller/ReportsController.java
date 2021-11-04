package socialnetwork.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Callback;
import socialnetwork.domain.FriendDateDTO;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.service.CommunityService;
import socialnetwork.utils.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class ReportsController
{
    public HBox hBoxMessages;
    public VBox vBoxMessages;
    User user;
    private CommunityService service;

    @FXML
    DatePicker startDate;
    @FXML
    DatePicker endDate;
    @FXML
    HBox hBox;
    @FXML
    VBox vBoxScroll;
    @FXML
    RadioButton choosen = new RadioButton();

    public PieChart friendsPie;
    public PieChart messagesPie;

    public void setService(CommunityService service, User user)
    {
        this.service=service;
        this.user = user;
        StringConverter myConverter = new StringConverter();
        startDate.setConverter(myConverter);
        endDate.setConverter(myConverter);
        this.startDate.getEditor().setFont(Font.font("Century Gothic", 12));
        this.endDate.getEditor().setFont(Font.font("Century Gothic", 12));
        this.endDate.getEditor().setAlignment(Pos.CENTER);
        this.startDate.getEditor().setAlignment(Pos.CENTER);
        Label lb = new Label("Check the user you want to see the conversation with:");
        ArrayList<String> str=new ArrayList<>();
        for(FriendDateDTO x: service.getFriendshipsByUserReports(user))
        {
            String n;
            n = x.getUserName();
            str.add(n);
        }
        vBoxScroll.setSpacing(5);
        lb.setFont(Font.font("Century Gothic", 12));
        lb.setTextFill(Paint.valueOf("#ffffff"));
        vBoxScroll.getChildren().add(lb);
        for (String s : str)
        {
            RadioButton rd = new RadioButton();
            Label new_label = new Label(s);
            rd.setGraphic(new_label);
            new_label.setFont(Font.font("Century Gothic"));
            new_label.setTextFill(Paint.valueOf("#ffffff"));
            vBoxScroll.getChildren().add(rd);

            EventHandler<ActionEvent> event = e -> {
                if (rd.isSelected())
                {
                    choosen.setSelected(false);
                   choosen=rd;
                }
                else
                {
                    choosen=new RadioButton();
                }

            };
            rd.setOnAction(event);
        }

    }

    public void generate_report()
    {
        try {
            vBoxMessages.getChildren().clear();
            LocalDate startDateValue = startDate.getValue();
            LocalDate endDateValue = endDate.getValue();
            ArrayList<FriendDateDTO> friendDateDTOArrayList = service.getFriendshipsByUserReports(user);
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (FriendDateDTO friendDateDTO : friendDateDTOArrayList)
                if (friendDateDTO.getDate().toLocalDate().compareTo(startDateValue) >= 0 && (friendDateDTO.getDate().toLocalDate().compareTo(endDateValue) <= 0)) {
                    boolean ok = false;
                    for (PieChart.Data d : pieChartData)
                        if (d.getName().equals(friendDateDTO.getDate().toLocalDate().toString())) {
                            d.setPieValue(d.getPieValue() + 1);
                            ok = true;
                        }
                    if (!ok)
                        pieChartData.add(new PieChart.Data(friendDateDTO.getDate().toLocalDate().toString(), 1f));
                }
            friendsPie.setTitle("Friends made over the time");
            friendsPie.setData(pieChartData);

            ArrayList<Message> messages = (ArrayList<Message>) service.see_your_message(user);
            ObservableList<PieChart.Data> pieChartData1 = FXCollections.observableArrayList();
            for (Message message : messages)
                if (message.getDate().toLocalDate().compareTo(startDateValue) >= 0 && (message.getDate().toLocalDate().compareTo(endDateValue) <= 0)) {
                    boolean ok = false;
                    for (PieChart.Data d : pieChartData1)
                        if (d.getName().equals(message.getDate().toLocalDate().toString())) {
                            d.setPieValue(d.getPieValue() + 1);
                            ok = true;
                        }
                    if (!ok)
                        pieChartData1.add(new PieChart.Data(message.getDate().toLocalDate().toString(), 1f));
                }
            messagesPie.setTitle("Messages sent over the time");
            messagesPie.setData(pieChartData1);
            vBoxMessages.setSpacing(5);
            try {
                User friend = service.getUserByUsername(((Label) choosen.getGraphic()).getText());
                Label l = new Label("Your messages with " + friend.getUsername() + " over this period of time are:");
                l.setFont(Font.font("Century Gothic", 13));
                vBoxMessages.getChildren().add(l);
                service.see_messages_by_user(user, friend).forEach(message ->
                {
                    Label label = new Label(message.getMessage() + " " + message.getDateConverted());
                    label.setFont(Font.font("Century Gothic", 12));
                    vBoxMessages.getChildren().add(label);
                });
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You have to choose a friend!", ButtonType.OK);
                alert.setResizable(true);
                alert.show();
            }
        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR,"You have to select a date!", ButtonType.OK);
            alert.setResizable(true);
            alert.show();
        }
    }

    public void export_report()
    {

        ListView<Node> nodeListView = new ListView<>();
        Label label = new Label("Report on "+user.getFirstName()+" "+user.getLastName()+"'s activity in the "+startDate.getValue().toString()+"/"+endDate.getValue().toString()+" period");
        label.setFont(Font.font("Century Gothic", 13));
        VBox vBoxf= new VBox();
        VBox vBoxm= new VBox();
        vBoxm.getChildren().add(messagesPie);
        vBoxf.getChildren().add(friendsPie);
        VBox vBox1 = new VBox();
        VBox vBox2 = new VBox();
        vBox1.getChildren().add(label);
        vBox1.getChildren().add(vBoxf);
        int total= friendsPie.getData().stream().mapToInt(d -> (int) d.getPieValue()).sum();
        Label l2= new Label("Total number of friends made in this period: "+total+".");
        l2.setFont(Font.font("Century Gothic", 13));
        vBox1.getChildren().add(l2);
        friendsPie.getData().forEach(d->{
            if(d.getPieValue()==1) {
                Label label2 = new Label("-On " + d.getName() + " " + user.getFirstName() + " made " + (int) d.getPieValue() + " friend.");
                label2.setFont(Font.font("Century Gothic", 12));
                vBox1.getChildren().add(label2);
            }
            else
            {
                Label label2 = new Label("-On " + d.getName() + " " + user.getFirstName() + " made " + (int) d.getPieValue() + " friends.");
                label2.setFont(Font.font("Century Gothic", 12));
                vBox1.getChildren().add(label2);
            }

        });
        vBox2.getChildren().add(vBoxm);
        int total2= messagesPie.getData().stream().mapToInt(d -> (int) d.getPieValue()).sum();
        Label l1=new Label("Total number of messages received or sent in this period: "+total2+".");
        l1.setFont(Font.font("Century Gothic", 13));
        vBox2.getChildren().add(l1);
        messagesPie.getData().forEach(d->{
            if(d.getPieValue()==1) {
                Label label2 = new Label("-On " + d.getName() + " " + user.getFirstName() + " received or sent " + (int) d.getPieValue() + " message.");
                label2.setFont(Font.font("Century Gothic", 12));
                vBox2.getChildren().add(label2);
            }
            else
            {
                Label label2 = new Label("-On " + d.getName() + " " + user.getFirstName() + " received or sent " + (int) d.getPieValue() + " messages.");
                label2.setFont(Font.font("Century Gothic", 12));
                vBox2.getChildren().add(label2);
            }

        });
        nodeListView.getItems().add(vBox1);
        nodeListView.getItems().add(vBox2);
        nodeListView.getItems().add(vBoxMessages);
        PrinterJob job = PrinterJob.createPrinterJob();
        if(job!=null)
        {
           nodeListView.getItems().forEach(node -> job.printPage(node));
            job.endJob();
        }

        this.hBox.getChildren().add(friendsPie);
        this.hBox.getChildren().add(messagesPie);
    }

}
