package fr.univpau.m2ti.sma.fishmarket.agent;

import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.gui.BidderView;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class BidderAgent extends Agent
{
	protected Logger logger =
			Logger.getLogger(BidderAgent.class.getName());


	private boolean bidsAutomatically = false;

	private Auction selectedAuction;

	private FSMBehaviour subscribeToAuctionFSM = null;
	private FSMBehaviour runningAuctionFSM = null;

	private BidderView bidderView;

	private float maxPrice;



	private static final String NO_MARKET_AVAILABLE =
			"Market agent couldn't be located.";

	private float biddingPrice;

	private boolean withinBiddingTimeFrame = false;
	private boolean answerBid = false;
	private boolean auctionSelected = false;



	@Override
	protected void setup()
	{
		// Look up market
		super.setup();

		//find new auction
		this.bidderView = new BidderView(this);
		this.bidderView.display();
	}

	@Override
	protected void takeDown()
	{
		this.bidderView.hide();

		this.bidderView.dispose();

		// Cancel auction
		super.takeDown();
	}

	/**
	 * Called at the end of the auction subscription process.
	 *
	 * @param maxPrice
	 */
	public void createBidderFSM(float maxPrice)
	{
		this.bidderView.initBidList(this.getSubscribedAuction());

		if (this.runningAuctionFSM != null)
		{
			this.removeBehaviour(this.runningAuctionFSM);
		}

		this.runningAuctionFSM = new RunningAuctionBidderFSMBehaviour(this);

		this.addBehaviour(this.runningAuctionFSM);

		//Disable find auction buttons
		this.bidderView.auctionState();
	}

	public void createAuctionFinderFSM()
	{
		if (this.subscribeToAuctionFSM != null)
		{
			this.removeBehaviour(this.subscribeToAuctionFSM);
		}


		this.subscribeToAuctionFSM = new SubscribeToAuctionBidderFSMBehaviour(this);
		// Register behaviour
		this.addBehaviour(this.subscribeToAuctionFSM);

	}

	public void auctionOver()
	{
		this.bidderView.findAuctionState();
	}

	public void displayAlert(String message)
	{
		this.bidderView.alert(message);
	}

	public void displayAuctionInformation(String message)
	{
		this.bidderView.addBidInformation(message);
	}



	/**
	 * Called by BidderView when refresh button is used.
	 */
	public void refreshAuctionList()
	{
		this.createAuctionFinderFSM();
	}

	/**
	 * Called by PickAuctionBehaviour.
	 * @param auctionList
	 */
	public void displayAuctionList(HashSet<Auction> auctionList)
	{
		this.bidderView.displayAuctionList(auctionList);
	}

	public void setBidsAutomatically(boolean bidsAutomatically)
	{
		this.bidsAutomatically = bidsAutomatically;
	}

	public boolean bidsAutomatically()
	{
		return this.bidsAutomatically;
	}

	public void setAuctionSelected(boolean auctionSelected)
	{
		this.auctionSelected = auctionSelected;
	}

	public boolean hasAuctionSelected()
	{
		return auctionSelected;
	}

	/**
	 * Called by BidderView when an auction is selected and the suscribe
	 * button is used.
	 *
	 * @param selectedAuction
	 */
	public void subscribeToAuction(Auction selectedAuction)
	{
		this.selectedAuction = selectedAuction;
		this.auctionSelected = true;

//		//notify behaviour by sending a message
//		ACLMessage unblockMessage = new ACLMessage(FishMarket.Performatives.TO_ANNOUNCE);
//		unblockMessage.addReceiver(SubscribeToAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
//		unblockMessage.addReceiver(this.getAID());
//		this.send(unblockMessage);
	}

	public Auction getSubscribedAuction()
	{
		return this.selectedAuction;
	}

	/**
	 * Called by BidderView when the bid button is clicked.
	 */
	public void takeUserBidIntoAccount()
	{
		this.answerBid = true;
	}

	public boolean isWithinBiddingTimeFrame()
	{
		return withinBiddingTimeFrame;
	}

	public void setWithinBiddingTimeFrame(boolean withinBiddingTimeFrame)
	{
		this.withinBiddingTimeFrame = withinBiddingTimeFrame;
	}

	public boolean answerBid()
	{
		return answerBid;
	}

	public void setAnswerBid(boolean answerBid)
	{
		this.answerBid = answerBid;
	}

	/**
	 * Called when a new bid is received.
	 *
	 * @param information
	 */
	public void displayBidInformation(String information)
	{
		this.bidderView.addBidInformation(information);
	}

	/**
	 * Displays an alert signaling that market is not available.
	 */
	public void alertNoMarket()
	{
		this.bidderView.alert(BidderAgent.NO_MARKET_AVAILABLE);
	}

	public float getMaxPrice()
	{
		return maxPrice;
	}

	public void setMaxPrice(float maxPrice)
	{
		this.maxPrice = maxPrice;
	}

	/**
	 * Returns the price which came with the last to_announce.
	 *
	 * @return
	 */
	public float getBiddingPrice()
	{
		return this.biddingPrice;
	}

	/**
	 * Called by behaviours when a new announce is received.
	 */
	public void handleAnnounce(float price)
	{
		this.biddingPrice = price;

		this.bidderView.addBidInformation("New price announce " + String.valueOf(price));

		if (this.bidsAutomatically())
		{
			//disable bid button
			this.bidderView.disableBidButton();
		}
		else
		{
			//enable bid button
			this.bidderView.enableBidButton();
		}
	}


	public void updateAuctionStatus(String status)
	{
		this.bidderView.updateRunningAuctionStatus(status);
	}

	public AID getMarketAgentAID()
	{
		AID marketAgent = null;

		DFAgentDescription marketTemplate = new DFAgentDescription();
		ServiceDescription marketSd = new ServiceDescription();

		// Template for searching the market agent
		marketSd.setType(MarketAgent.SERVICE_DESCIPTION);
		marketTemplate.addServices(marketSd);

		try
		{
			DFAgentDescription[] marketResult = DFService.search(this, marketTemplate);

			if(marketResult.length == 1)
			{
				marketAgent = marketResult[0].getName();
			}
			else
			{
				logger.log(Level.SEVERE, null, "Logic error: multiple market agents found !");
			}
		}
		catch (FIPAException fe)
		{
			logger.log(Level.SEVERE, null, fe);
		}

		return marketAgent;
	}

}
