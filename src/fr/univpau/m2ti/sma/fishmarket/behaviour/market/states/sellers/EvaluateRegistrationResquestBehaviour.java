package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class EvaluateRegistrationResquestBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private SellerManagementBehaviour myFSM;
	
	/** Tells whether the creation of the auction is confirmed or not. */
	private boolean isCreationAccepted;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(EvaluateRegistrationResquestBehaviour.class.getName());
	
	/**
	 * Creates a behaviour which is to be associated with a state of the 
	 * market agent's FSM behaviour.
	 * 
	 * @param myMarketAgent
	 * 			the market agent of which FSM behavior's state this behaviour is to be associated.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public EvaluateRegistrationResquestBehaviour(
			MarketAgent myMarketAgent,
			SellerManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		MarketAgent myMarketAgent = (MarketAgent)myAgent;
		
		Auction auction = null;
		
		try
		{
			auction = (Auction)
					this.myFSM.getRequest().getContentObject();
		}
		catch (UnreadableException e)
		{
			EvaluateRegistrationResquestBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
		
		if(! myMarketAgent.isRegisteredAuction(auction))
		{
			this.isCreationAccepted = true;
		}
		else
		{
			this.isCreationAccepted = false;
		}
	}
	
	@Override
	public int onEnd()
	{
		return this.isCreationAccepted ?
				SellerManagementBehaviour.TRANSITION_CONFIRM_AUCTION_REGISTRATION :
					this.replyRefuse();
	}
	
	/**
	 * Sends a refuse reply to the seller agent which requested the registration of an auction.
	 * 
	 * @return the code of the transition after a refusal of the registration of an auction.
	 */
	private int replyRefuse()
	{
		// Reply
		ACLMessage reply =
				this.myFSM.getRequest().createReply();
		
		reply.setPerformative(
				FishMarket.Performatives.TO_REFUSE);
		
		super.myAgent.send(reply);
		
		return SellerManagementBehaviour.TRANSITION_REFUSE_AUCTION_REGISTRATION_REQUEST;
	}
}