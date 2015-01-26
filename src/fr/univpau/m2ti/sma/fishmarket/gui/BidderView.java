package fr.univpau.m2ti.sma.fishmarket.gui;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 */
public class BidderView
{
    private JPanel panel1;
    private JPanel subscriptionPane;
    private JPanel auctionPane;
    private JLabel subscriptionPaneTitle;
    private JList auctionList;
    private JButton subscribeButton;
    private JButton refreshButton;
    private JButton bidButton;
    private JList bidList;
    private JLabel bidListLabel;
    private JCheckBox autoBidCheckBox;
    private JTextField maximumPriceTextField;

    private static String AUTO_BID_MAX_PRICE_NOT_SET =
            "Please set a maximum price";

    private JFrame currentFrame;

    private static String REFRESH_ACTION =
            "refreshAction";
    private static String SUBSCRIBE_ACTION =
            "subscribeAction";


    private DefaultListModel<String> auctionListModel;

    private DefaultListModel<String> bidListModel;

    private BidderAgent bidderAgent;

    private Map<String, Auction> auctions = new HashMap<String, Auction>();

    public BidderView(BidderAgent bidderAgent)
    {
        this.bidderAgent = bidderAgent;

        this.createWindow();
    }


    private void createWindow()
    {
        this.createSubscriptionPane();
        this.createAuctionPane();
        this.panel1 = new JPanel();
        this.panel1.setLayout(
                new BoxLayout(this.panel1, BoxLayout.LINE_AXIS)
        );
        this.panel1.setPreferredSize(new Dimension(600, 450));
        this.panel1.add(this.subscriptionPane);
        this.panel1.add(this.auctionPane);

        this.currentFrame = new JFrame("Bidder agent : " + this.bidderAgent.getLocalName());
        this.currentFrame.setContentPane(this.panel1);
        this.currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.currentFrame.pack();


        this.attachListeners();

        this.findAuctionState();

    }

    public void dispose()
    {
        this.currentFrame.dispose();
    }

    private static int PANE_WIDTH = 150;
    private static int PANE_HEIGHT = 450;
    private static int JSCROLL_PANE_WIDTH = 130;
    private static int JSCROLL_PANE_HEIGHT = 350;

    private void createSubscriptionPane()
    {
        this.subscriptionPane = new JPanel();
        this.subscriptionPane.setLayout(
                new BoxLayout(this.subscriptionPane, BoxLayout.PAGE_AXIS)
        );
        this.subscriptionPane.setPreferredSize(new Dimension(PANE_WIDTH,PANE_HEIGHT));

        this.subscriptionPaneTitle = new JLabel("Pick an auction");
        this.subscriptionPane.add(this.subscriptionPaneTitle);
        //to add
        JScrollPane listScroll = new JScrollPane();
        listScroll.setPreferredSize(new Dimension(JSCROLL_PANE_WIDTH, JSCROLL_PANE_HEIGHT));
        this.auctionList = new JList();
        this.auctionListModel = new DefaultListModel<String>();
        this.auctionList.setModel(this.auctionListModel);
        listScroll.setViewportView(this.auctionList);
        this.subscriptionPane.add(listScroll);

        this.subscribeButton = new JButton("Subscribe");
        this.subscribeButton.setEnabled(false);
        this.subscriptionPane.add(this.subscribeButton);

        this.refreshButton = new JButton("Refresh");
        this.subscriptionPane.add(this.refreshButton);
    }

    private void createAuctionPane()
    {
        this.auctionPane = new JPanel();
        this.auctionPane.setLayout(
                new BoxLayout(this.auctionPane, BoxLayout.PAGE_AXIS)
        );
        this.auctionPane.setPreferredSize(new Dimension(PANE_WIDTH,PANE_HEIGHT));

        this.bidListLabel = new JLabel();
        this.auctionPane.add(this.bidListLabel);

        JScrollPane listScroll = new JScrollPane();
        listScroll.setPreferredSize(new Dimension(JSCROLL_PANE_WIDTH, JSCROLL_PANE_HEIGHT));
        this.bidList = new JList();
        this.bidListModel = new DefaultListModel<String>();
        this.bidList.setModel(this.bidListModel);
        listScroll.setViewportView(this.bidList);
        this.auctionPane.add(listScroll);

        this.bidButton = new JButton("Bid");

        this.auctionPane.add(this.bidButton);

        JPanel autoBidPane = new JPanel();
        autoBidPane.setLayout(
                new BoxLayout(autoBidPane, BoxLayout.LINE_AXIS)
        );
        this.autoBidCheckBox = new JCheckBox("Auto-bid");
        autoBidPane.add(this.autoBidCheckBox);
        this.maximumPriceTextField = new JTextField();
        autoBidPane.add(this.maximumPriceTextField);

        this.auctionPane.add(autoBidPane);

    }

    public void prepare()
    {
        this.currentFrame = new JFrame("AuctionPicker");
        this.currentFrame.setContentPane(this.panel1);
        this.currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.currentFrame.pack();
        this.attachListeners();

        this.findAuctionState();
    }

    public void findAuctionState()
    {
        this.subscribeButton.setEnabled(false);
        this.refreshButton.setEnabled(true);
        this.disableBidButton();
    }

    public void auctionState()
    {
        this.subscribeButton.setEnabled(false);
        this.refreshButton.setEnabled(false);
    }

    public void display()
    {
        this.currentFrame.setVisible(true);
    }

    public void hide()
    {
        this.currentFrame.setVisible(false);
    }

    public void attachListeners()
    {

        this.refreshButton.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent)
                    {
                        bidderAgent.refreshAuctionList();

                        //ensure subscribe button is disabled
                        subscribeButton.setEnabled(false);
                    }
                }
        );

        this.auctionList.addListSelectionListener(
                new ListSelectionListener()
                {
                    @Override
                    public void valueChanged(
                            ListSelectionEvent listSelectionEvent
                    )
                    {
                        //enable Subscribe button
                        subscribeButton.setEnabled(true);
                    }
                }
        );
        this.subscribeButton.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent)
                    {
                        int selectedAuctionIndex =
                                auctionList.getSelectedIndex();
                        String selectedAuctionName =
                                (String) auctionList.getSelectedValue();
                        Auction selectedAuction =
                                auctions.get(selectedAuctionName);

                        bidderAgent.subscribeToAuction(selectedAuction);

                        subscribeButton.setEnabled(false);
                    }
                }
        );

        this.bidButton.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent)
                    {
                        bidderAgent.takeUserBidIntoAccount();
                        bidButton.setEnabled(false);
                    }
                }
        );

        this.autoBidCheckBox.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent)
                    {
                        if (autoBidCheckBox.isSelected())
                        {
                            //Auto bid requested

                            //is max price set ?
                            String maxPriceString =
                                    maximumPriceTextField.getText().trim();
                            float maxPrice = Float.parseFloat(
                                    maxPriceString.equals("") ? "0"
                                            : maxPriceString
                            );

                            if (maxPrice <= 0)
                            {
                                alert(BidderView.AUTO_BID_MAX_PRICE_NOT_SET);

                                //Uncheck checkbox
                                autoBidCheckBox.setSelected(false);
                                //Focus max price field
                                maximumPriceTextField.requestFocus();
                            }
                            else
                            {
                                // max price is set
                                bidderAgent.setMaxPrice(maxPrice);
                                bidderAgent.setBidsAutomatically(true);

                                //disable max price field
                                maximumPriceTextField.setEnabled(false);
                                //Disable bid button
                                disableBidButton();
                            }
                        }
                        else
                        {
                            // Auto bid cancelled.
                            bidderAgent.setBidsAutomatically(false);

                            //enable max price field
                            maximumPriceTextField.setEnabled(true);

                        }
                    }
                }
        );
    }

    public void displayAuctionList(HashSet<Auction> auctions)
    {
        this.auctionListModel.clear();

        this.auctions.clear();

        for (Auction a : auctions)
        {
            this.auctionListModel.addElement(a.getID());
            this.auctions.put(a.getID(), a);
        }
    }

    public void clearBidList()
    {
        this.bidListModel.clear();
    }

    /**
     * Entered auction.
     *
     * @param auction
     */
    public void initBidList(Auction auction)
    {
        this.clearBidList();
        this.bidListLabel.setText(auction.getAuctionName());

        //Remove auction from list of subscribable auctions
        this.auctionListModel.removeElement(auction.getID());
        this.auctions.remove(auction);

        //Remove focus from list element
        this.auctionList.clearSelection();
    }

    public void addBidInformation(String information)
    {
        this.bidListModel.addElement(information);
    }

    public void enableBidButton()
    {
        this.bidButton.setEnabled(true);
    }

    public void disableBidButton()
    {
        this.bidButton.setEnabled(false);
    }

    public void alert(String message)
    {
        JOptionPane.showMessageDialog(null, message);
    }

}
