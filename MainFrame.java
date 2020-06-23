import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.util.ArrayList;

public class MainFrame extends Frame {

    static Calendar c = Calendar.getInstance();
    static MonthHeader monthHeader = new MonthHeader(c);
    static YearChangePanel yearChangePanel = new YearChangePanel(c);
    Button nextMonth = new Button("Next Month");
    Button prevMonth = new Button("Prev Month");
    static MonthPanel monthPanel = new MonthPanel(c);

    MainFrame() {

        nextMonth.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                c.set(Calendar.MONTH, monthPanel.getCalendarObject().get(Calendar.MONTH));
                redraw();
            }
        });
        prevMonth.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                c.set(Calendar.MONTH, monthPanel.getCalendarObject().get(Calendar.MONTH) - 2);
                redraw();
            }
        });

        setLayout(new BorderLayout());
        add(monthHeader, BorderLayout.NORTH);
        add(yearChangePanel, BorderLayout.SOUTH);
        add(nextMonth, BorderLayout.EAST);
        add(monthPanel, BorderLayout.CENTER);
        add(prevMonth, BorderLayout.WEST);

        setSize(500, 500);
        setTitle("Calendar");
        setVisible(true);
    }

    static void redraw() {
        yearChangePanel.updateYear(c);
        monthHeader.redraw(c);
        monthPanel.setCalendar(c);
    }

    static void updateYearCallback(int year) {
        c.set(Calendar.YEAR, year);
        redraw();
    }

    public static void main(String args[]) {
        MainFrame f = new MainFrame();

        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                f.dispose();
            }
        });
    }
}

class MonthHeader extends Panel {

    Calendar cal;
    Label month;

    MonthHeader(Calendar c) {
        cal = c;
        month = new Label(getMonth());
        month.setAlignment(Label.CENTER);
        setLayout(new GridLayout(3, 3));
        add(new Label(""));
        add(new Label(""));
        add(new Label(""));
        add(new Label(""));
        add(month);
        add(new Label(""));
        add(new Label(""));
        add(new Label(""));
        add(new Label(""));
    }

    String getMonth() {
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }

    void redraw(Calendar c) {
        cal = c;
        month.setText(getMonth());
    }
}

class YearChangePanel extends Panel {

    public static final int MAX_YEAR = 2200;
    public static final int MIN_YEAR = 1900;
    Choice yearChoice;

    YearChangePanel(Calendar c) {
        Label yearChange = new Label("Select Year to change: ");
        setLayout(new GridLayout(3, 2));
        add(new Label(""));
        add(new Label(""));
        add(yearChange);
        yearChoice = new Choice();
        for (int i = MIN_YEAR; i < MAX_YEAR; i++) {
            yearChoice.add(String.valueOf(i));
        }
        yearChoice.select(String.valueOf(c.get(Calendar.YEAR)));
        add(yearChoice);
        add(new Label(""));
        add(new Label(""));

        yearChoice.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                MainFrame.updateYearCallback(Integer.parseInt(yearChoice.getSelectedItem().trim()));
            }
        });
    }

    void updateYear(Calendar c) {
        yearChoice.select(String.valueOf(c.get(Calendar.YEAR)));
    }
}

class MonthPanel extends Panel {

    Calendar c;
    DayPanel[] dayArray = new DayPanel[42];

    MonthPanel(Calendar cal) {
        c = cal;
        setLayout(new GridLayout(7, 7));
        draw();
    }

    void setCalendar(Calendar cal) {
        c = cal;
        redraw();
    }

    Calendar getCalendarObject() {
        return c;
    }

    int getStart() {
        Calendar temp = c;
        temp.set(Calendar.DAY_OF_MONTH, 1);
        return temp.get(Calendar.DAY_OF_WEEK);
    }

    void draw() {
        int start = getStart();
        int end = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        add(new WeekHeaderPanel("Sun"));
        add(new WeekHeaderPanel("Mon"));
        add(new WeekHeaderPanel("Tue"));
        add(new WeekHeaderPanel("Wed"));
        add(new WeekHeaderPanel("Thu"));
        add(new WeekHeaderPanel("Fri"));
        add(new WeekHeaderPanel("Sat"));
        for (int i = 0; i < 42; i++) {
            if (i >= start - 1 && i - start < end - 1) {
                dayArray[i] = new DayPanel(c.getTime(), 1);
                c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
            } else {
                dayArray[i] = new DayPanel();
            }
            if (i % 7 == 0 || i % 7 == 6) {
                dayArray[i].setBackground(new Color(255, 220, 220));
            } else if (i % 2 == 0) {
                dayArray[i].setBackground(new Color(249, 244, 244));
            } else {
                dayArray[i].setBackground(new Color(255, 255, 255));
            }
            add(dayArray[i]);
        }
    }

    void redraw() {
        int start = getStart();
        int end = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < 42; i++) {
            dayArray[i].clear();
            if (i >= start - 1 && i - start < end - 1) {
                dayArray[i].update(c.getTime(), 2);
                c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
            }
        }
    }
}

class WeekHeaderPanel extends Panel {

    WeekHeaderPanel(String week) {
        Label weekLabel = new Label(week);
        add(weekLabel);
    }
}

class DayPanel extends Panel {

    Date date;
    int eventCount;
    Label dayLabel, eventLabel;

    DayPanel(Date date, int eventCount) {

        this.date = date;
        this.eventCount = eventCount;

        dayLabel = new Label("");
        if (date != null) {
            dayLabel.setText("" + this.date.getDate());
        }

        eventLabel = new Label(String.valueOf(""));
        if (this.eventCount > 0) {
            eventLabel.setText(String.valueOf(this.eventCount));
        }

        Label emptyLabel1 = new Label("");
        Label emptyLabel2 = new Label("");

        eventLabel.setAlignment(Label.RIGHT);
        setLayout(new GridLayout(2, 2));

        add(dayLabel);
        add(emptyLabel1);
        add(emptyLabel2);
        add(eventLabel);

        DateClickListener dateClickListener = new DateClickListener(this);

        dayLabel.addMouseListener(dateClickListener);
        emptyLabel1.addMouseListener(dateClickListener);
        emptyLabel2.addMouseListener(dateClickListener);
        eventLabel.addMouseListener(dateClickListener);
    }

    DayPanel(Date date) {
        this(date, 0);
    }

    DayPanel() {
        this(null, 0);
    }

    void update(Date d, int e) {
        date = d;
        eventCount = e;

        if (date != null) {
            dayLabel.setText("" + this.date.getDate());
        } else {
            dayLabel.setText("");
        }

        if (this.eventCount > 0) {
            eventLabel.setText(String.valueOf(this.eventCount));
        } else {
            eventLabel.setText("");
        }
    }

    void clear() {
        update(null, 0);
    }

    private class DateClickListener implements MouseListener {

        DayPanel dayPanel;

        DateClickListener(DayPanel date) {
            dayPanel = date;
        }

        public void mouseClicked(MouseEvent event) {
            if (dayPanel.date != null) {
                System.out.println(dayPanel.date.getDate());
            }
        }

        public void mouseEntered(MouseEvent event) {
        }

        public void mouseExited(MouseEvent event) {
        }

        public void mousePressed(MouseEvent event) {
        }

        public void mouseReleased(MouseEvent event) {
        }
    }
}
