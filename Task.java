package RSS;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;


    public class Task extends JFrame {
        JTextField tf;
        DefaultListModel listModel;
        JList list;
        JButton button;
        JPanel pane;
        int name;
        boolean newflg;
        boolean flgOnce;
        ArrayList<Item> items = new ArrayList<Item>();  //検索でしぼられたアイテムをまとめる
        public static int siteNum = 5;
        JCheckBox[] checkboxArray = new JCheckBox[siteNum];
        String[]  titles = {"gigazine", "lifehacker", "techcrunch", "engadget", "@IT"};
        Task[] task = new Task[siteNum];
        String[] links = {"http://gigazine.net/news/rss_2.0/", "http://feeds.lifehacker.jp/rss/lifehacker/index.xml", "http://jp.techcrunch.com/feed/",
                "http://japanese.engadget.com/rss.xml", "http://rss.rssad.jp/rss/itmatmarkit/rss.xml"};

        public static void main(String[] args) {
            JFrame w = new Task( "Task" );
            w.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            w.setSize( 800, 600 );
            w.setVisible( true );

        }


        public void search(String word2) {
            String word = word2;

            // 複数のサイトから検索
            Feed feed;
            ArrayList<Item> list = new ArrayList<Item>(); //それぞれのitem

            newflg = true;

            for(int i = 0; i < siteNum; i++) {
                boolean isSearchTarget = checkboxArray[i].isSelected();
                // Checkboxにチェックがついていなければcontinue
                if (!isSearchTarget) {
                    continue;
                }
                flgOnce = true;
                System.out.println(titles[i]);
                name = i;
                feed = new Feed();
                feed.setURL(links[i]);
                feed.run();
                list.clear();
                list = feed.getItemList();
                for (Item item : list) {
                    System.out.print(".");
                    if (!item.getLink().contains("rss.rssad.jp/rss/ad")) {  //広告を除外
                        if (!ContextFinder.findContext(item.getLink(), word, name).equals("null")) {
                            if (flgOnce){
                                listModel.addElement(titles[i]);
                                flgOnce = false;
                            }
                            showItem(item, newflg);
                        }
                    }
                }
                System.out.println("\n");
                if (!flgOnce) listModel.addElement("　");    //新しい要素が無ければ改行不要
            }
        }

        public void showItem(Item item, boolean newflg) {
            if (newflg) {
                System.out.println("");
                items.add(item);
            }
            System.out.println(item.getDate());
            System.out.println(item.getTitle());
            System.out.println(item.getLink());
            listModel.addElement(item.getDate());
            listModel.addElement(item.getTitle());
            listModel.addElement(item.getLink());
        }

        class ItemComparator implements Comparator<Item> {
            public int compare(Item item1, Item item2){
                int rank;
                if (item1.getDate().compareTo(item2.getDate()) > 0){
                    rank = -1;
                }else if (item1.getDate().compareTo(item2.getDate()) < 0){
                    rank = 1;
                }else{
                    rank = 0;
                }
                return rank;
            }
        }

        // GUI
        public Task(String title ) {
            super( title );

            pane = (JPanel)getContentPane();
            pane.setLayout(new BorderLayout());

            JMenuBar menuBar = new JMenuBar();
            setJMenuBar( menuBar );
            JMenu fileMenu = new JMenu( "終了" );
            menuBar.add( fileMenu );
            JMenuItem item;
            item = new JMenuItem( new ExitAction() );
            fileMenu.add( item );


            JPanel header = new JPanel();
            JPanel fields = new JPanel();
            header.setLayout(new BorderLayout());
            fields.setLayout(new BoxLayout(fields, BoxLayout.X_AXIS));
            tf = new JTextField();
            tf.setBorder(new TitledBorder("検索欄"));
            fields.add(tf);
            button = new JButton("検索");
            button.addActionListener( new SearchWord());
            fields.add(button);
            header.add(fields, BorderLayout.NORTH);

            JPanel boxes = new JPanel();
            boxes.setLayout(new BoxLayout(boxes, BoxLayout.X_AXIS));


            for (int i = 0; i < siteNum; i++){
                checkboxArray[i] = new JCheckBox(titles[i], true);
                boxes.add(checkboxArray[i]);
            }

            header.add(boxes, BorderLayout.SOUTH);
            pane.add(header, BorderLayout.NORTH);

            listModel = new DefaultListModel();
            list = new JList( listModel );
            JScrollPane sc = new JScrollPane( list );
            sc.setBorder( new TitledBorder( "結果一覧" ) );
            pane.add( sc );

        }

        class SearchWord implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                if (tf.getText().equals("")){
                    System.out.println("フィード全体を取得");
                    listModel.addElement("フィード全体を取得");
                }else {
                    System.out.println(tf.getText() + "で検索");
                    listModel.addElement(tf.getText() + "で検索");
                }
                search(tf.getText());
                if (!items.isEmpty()) {
                    Collections.sort(items, new ItemComparator());//最新順でソート
                    newflg = false;
                    System.out.println("最新順でソート");
                    listModel.addElement("最新順でソート");
                    for (Item item : items) {
                        showItem(item, newflg);
                    }
                }else {
                    System.out.println("検索がヒットしませんでした");
                    listModel.addElement("検索がヒットしませんでした");
                }
                items.clear();
                System.out.println("\n");   //余白
                listModel.addElement("　");
                listModel.addElement("　");
            }
        }

        class ExitAction extends AbstractAction {
            ExitAction() {
                putValue( Action.NAME, "終了" );
                putValue( Action.SHORT_DESCRIPTION, "終了" );
            }
            public void actionPerformed( ActionEvent e) {
                int ans = JOptionPane.showConfirmDialog(pane, "プログラムを終了しますか？", "プログラム終了", JOptionPane.OK_CANCEL_OPTION);
                if(ans == 0){
                    System.exit(0);
                }
            }
        }
    }