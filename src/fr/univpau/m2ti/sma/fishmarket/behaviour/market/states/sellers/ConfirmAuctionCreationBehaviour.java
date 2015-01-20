package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * A behavior which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class ConfirmAuctionCreationBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private SellerManagementBehaviour myFSM;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(ConfirmAuctionCreationBehaviour.class.getName());
	
	/**
	 * Creates a behaviour which is to be associated with a state of the 
	 * market agent's FSM behaviour.
	 * 
	 * @param myMarketAgent
	 * 			the market agent of which FSM behavior's state this behaviour is to be associated.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public ConfirmAuctionCreationBehaviour(
			MarketAgent myMarketAgent,
			SellerManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
		
		try
		{
			Auction auction =
					this.myFSM.getAuctionCreationRequest();
			
			msg.addReceiver(auction.getSellerID());
			msg.setContentObject(auction);
			
			// De-register auction request
			this.myFSM.setAuctionCreationRequest(null);
			
			super.myAgent.send(msg);
		}
		catch (IOException e)
		{
			ConfirmAuctionCreationBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
	}
}