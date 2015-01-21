package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
/**
 * A behavior which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class ConfirmRegistrationBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private SellerManagementBehaviour myFSM;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(ConfirmRegistrationBehaviour.class.getName());
	
	/**
	 * Creates a behaviour which is to be associated with a state of the 
	 * market agent's FSM behaviour.
	 * 
	 * @param myMarketAgent
	 * 			the market agent of which FSM behavior's state this behaviour is to be associated.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public ConfirmRegistrationBehaviour(
			MarketAgent myMarketAgent,
			SellerManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		ACLMessage msg = this.myFSM.getRequest();
		MarketAgent myMarketAgent = (MarketAgent)
				super.myAgent;
		
		Auction auction = null;
		
		try
		{
			auction = (Auction)
					msg.getContentObject();
		}
		catch (UnreadableException e)
		{
			ConfirmRegistrationBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
		
		auction.setStatus(
				Auction.STATUS_RUNNING);
		
		// Register auction
		myMarketAgent.registerAuction(auction);
		
		// Create FSM Behaviour to manage the auction
		myMarketAgent.addBehaviour(
				new AuctionManagementBehaviour(myMarketAgent, msg.getSender()));
		
		// Send reply
		ACLMessage reply = msg.createReply();
		reply.setPerformative(
				FishMarket.Performatives.TO_ACCEPT);
		reply.setConversationId( // Inform about the conversation ID of the auction
				AuctionManagementBehaviour.createConversationId(
						msg.getSender()));
		
		myMarketAgent.send(reply);
	}
}