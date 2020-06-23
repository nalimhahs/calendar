import java.awt.*;
import java.awt.event.*;
import java.util.Date;

import javax.swing.BoxLayout;

import java.util.ArrayList;

public class EventFrame extends Frame {

    EventListPanel eventListPanel;
    DataStore dbConnector;

    EventFrame(Date date, DataStore dbConnector) {

        this.dbConnector = dbConnector;

        Label heading = new Label("My Events for " + date.toString());
        heading.setAlignment(Label.CENTER);
        add(heading);

        eventListPanel = new EventListPanel(date, dbConnector);

        add(eventListPanel);

        setSize(500, 500);
        setTitle("Events");
        setVisible(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public static void main(String args[]) {
        EventFrame f = new EventFrame(new Date(), new DataStore());
    }
}

class EventListPanel extends Panel {

    DataStore dbConnector;

    ArrayList<Event> events = new ArrayList<Event>();
    Date date;
    ArrayList<SingleEventPanel> singleEventPanelList = new ArrayList<SingleEventPanel>();

    EventListPanel(Date date, DataStore dbConnector) {
        this.dbConnector = dbConnector;
        this.date = date;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        redraw();
    }

    void fetchData() {
        // call data store to fetch data
        events = dbConnector.fetch(date);
    }

    void clearCurrentList() {
        for (SingleEventPanel p : singleEventPanelList) {
            this.remove(p);
        }
        singleEventPanelList.removeAll(singleEventPanelList);
    }

    void buildList() {
        fetchData();
        int i = 1;
        for (Event event : this.events) {
            singleEventPanelList.add(new SingleEventPanel(event, i++, this));
        }
    }

    void deleteCallback(int id) {
        for (SingleEventPanel p : singleEventPanelList) {
            if (p.event.id == id) {
                // call delete function here
                dbConnector.delete(p.event);
                break;
            }
        }
        redraw();
    }

    void addCallback(String text){
        dbConnector.add(text, date);
        redraw();
    }

    void redraw() {
        clearCurrentList();
        buildList();
        for (SingleEventPanel p : singleEventPanelList) {
            p.setVisible(true);
            this.add(p);
        }
        this.revalidate();
    }
}

class SingleEventPanel extends Panel implements ActionListener {

    int index;
    Event event;
    Button deleteButton = new Button("Delete");
    EventListPanel parent;

    SingleEventPanel(Event e, int i, EventListPanel parent) {
        index = i;
        this.parent = parent;
        event = e;
        Label eventText = new Label(e.event_description);
        Label eventIndex = new Label(String.valueOf(index));
        setLayout(new BorderLayout());
        add(eventIndex, BorderLayout.WEST);
        add(eventText, BorderLayout.CENTER);
        add(deleteButton, BorderLayout.EAST);
        deleteButton.addActionListener(this);
    }

    int getIndex() {
        return index;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == deleteButton) {
            parent.deleteCallback(event.id);
        }
    }
}

class AddEventPanel extends Panel implements ActionListener {

    TextField tf = new TextField();
    Label tfLabel = new Label("Enter event: ");
    Button addButton = new Button("Add");
    
    EventListPanel parent;

    AddEventPanel(EventListPanel parent) {
        this.parent = parent;

        
        setLayout(new BorderLayout());
        add(tfLabel, BorderLayout.WEST);
        add(tf, BorderLayout.CENTER);
        add(addButton, BorderLayout.EAST);
        addButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            parent.addCallback(tf.getText());
        }
    }
}