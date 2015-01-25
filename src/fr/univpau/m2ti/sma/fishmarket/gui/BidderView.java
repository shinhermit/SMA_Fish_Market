package fr.univpau.m2ti.sma.fishmarket.gui;

import fr.univpau.m2ti.sma.fishmarket.agent.BidderAgent;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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

        this.auctionListModel = new DefaultListModel<String>();
        this.bidListModel = new DefaultListModel<String>();

        this.auctionList.setModel(this.auctionListModel);
        this.bidList.setModel(this.bidListModel);

        this.prepare();
    }

    public void prepare()
    {
        this.currentFrame = new JFrame("AuctionPicker");
        this.currentFrame.setContentPane(this.panel1);
        this.currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.currentFrame.pack();
        this.attachListeners();

        this.initialState();
    }

    public void initialState()
    {
        this.subscribeButton.setEnabled(false);
        this.disableBidButton();
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
                        int selectedAuctionIndex = auctionList.getSelectedIndex();
                        String selectedAuctionName = (String) auctionList.getSelectedValue();
                        Auction selectedAuction = auctions.get(selectedAuctionName);

                        bidderAgent.subscribeToAuction(selectedAuction);
                    }
                }
        );

        this.bidButton.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent)
                    {
                        bidderAgent.sendBid();
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
                            String maxPriceString = maximumPriceTextField.getText().trim();
                            float maxPrice = Float.parseFloat(
                                    maxPriceString.equals("") ? "0" : maxPriceString
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
        this.auctionListModel.removeElement(auction);
        this.auctionList.repaint();

        //Remove focus from list element
        this.auctionList.clearSelection();
        //Grey out subscribe button
        this.subscribeButton.setEnabled(false);
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