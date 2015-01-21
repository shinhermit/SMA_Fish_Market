package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.registration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.RegisterAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class RequestRegistrationBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private RegisterAuctionBehaviour myFSM;
	
	/** The transition which will be selected. */
	private int transition;
	
	/** The maximal number of attempts to register an auction. */
	private static final int MAX_REQUEST_COUNT = 4;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(RequestRegistrationBehaviour.class.getName());
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public RequestRegistrationBehaviour(
			SellerAgent mySellerAgent,
			RegisterAuctionBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		SellerAgent mySellerAgent =
				(SellerAgent) super.myAgent;
		
		ACLMessage mess = new ACLMessage(
				FishMarket.Performatives.REQUEST_AUCTION_REGISTRATION);
		
		// Set topic
		mess.addReceiver(SellerManagementBehaviour.MESSAGE_TOPIC);
		
		// Receiver
		mess.addReceiver(mySellerAgent.getMarketAgent());
		
		// Add auction and send
		try
		{
			mess.setContentObject(
					new Auction(mySellerAgent.getAID()));
			
			mySellerAgent.send(mess);
		}
		catch (IOException e)
		{
			RequestRegistrationBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
		
		int requestCount = this.myFSM.notifyNewRequest();
		
		// Next transition
		this.transition =
				(requestCount >= RequestRegistrationBehaviour.MAX_REQUEST_COUNT) ?
				RegisterAuctionBehaviour.TRANSITION_TERMINATE_FAILURE :
					RegisterAuctionBehaviour.TRANSITION_REQUEST_SENT;
	}
	
	@Override
	public int onEnd()
	{
		return this.transition;
	}
}
