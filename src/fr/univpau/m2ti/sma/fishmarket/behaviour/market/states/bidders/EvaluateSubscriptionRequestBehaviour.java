package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import jade.core.AID;
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
public class EvaluateSubscriptionRequestBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private BidderManagementBehaviour myFSM;
	
	/** The status of the evaluation (in case of refuse). */
	private int status = BidderManagementBehaviour.STATUS_REFUSE_NOT_REFUSED;
	
	/** Tells whether the subscription has been accepted or not. */
	private boolean subscriptionAccepted;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(EvaluateSubscriptionRequestBehaviour.class.getName());
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public EvaluateSubscriptionRequestBehaviour(
			MarketAgent myMarketAgent,
			BidderManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		this.subscriptionAccepted = false;
		
		ACLMessage mess = this.myFSM.getRequest();
		
		MarketAgent myMarketAgent = (MarketAgent)
				super.myAgent;
		
		Object content = null;
		
		try
		{
			content = mess.getContentObject();
		}
		catch (UnreadableException e)
		{
			e.printStackTrace();
		}
		
		if(content != null)
		{
			if(content instanceof AID)
			{
				AID seller = (AID) content;
				
				Auction requestedAuction =
						myMarketAgent.findAuction(seller);
				
				if(requestedAuction != null)
				{
					if(requestedAuction.getStatus() != Auction.STATUS_OVER
							&& requestedAuction.getStatus() != Auction.STATUS_CANCELLED
							&& !myMarketAgent.isSuscriber(
									seller, mess.getSender()))
					{
						this.subscriptionAccepted = true;  //// Accepted !!!!!!!!!!
					}
					
					
					else if(requestedAuction.getStatus() == Auction.STATUS_OVER)
					{
						this.status = 
								BidderManagementBehaviour.STATUS_REFUSE_AUCTION_OVER;
					}
					else if(requestedAuction.getStatus() == Auction.STATUS_CANCELLED)
					{
						this.status = 
								BidderManagementBehaviour.STATUS_REFUSE_AUCTION_CANCELLED;
					}
					else
					{
						this.status = 
								BidderManagementBehaviour.STATUS_REFUSE_ALREADY_REGISTERED;
					}
				}
				else
				{
					this.status = 
							BidderManagementBehaviour.STATUS_REFUSE_AUCTION_NOT_FOUND;
				}
			}
			else
			{
				this.status = 
						BidderManagementBehaviour.STATUS_REFUSE_SELLER_AID_NOT_UNDERSTOOD;
			}
		}
		else
		{
			this.status = 
					BidderManagementBehaviour.STATUS_REFUSE_SELLER_AID_NOT_UNDERSTOOD;
		}
	}
	
	@Override
	public int onEnd()
	{
		return (this.subscriptionAccepted) ?
				BidderManagementBehaviour.TRANSITION_CONFIRM_SUBSCRIPTION :
					this.replyRefuse();
	}
	
	/**
	 * Replies to the request by a refuse.
	 * 
	 * @return the refuse status code.
	 */
	private int replyRefuse()
	{
		ACLMessage mess = this.myFSM.getRequest();
		
		ACLMessage reply = mess.createReply();
		
		try
		{
			reply.setContentObject(new Integer(this.status));
		}
		catch (IOException e)
		{
			EvaluateSubscriptionRequestBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
		
		super.myAgent.send(reply);
		
		this.myFSM.setRequest(null);
		
		return BidderManagementBehaviour.TRANSITION_REFUSE_SUBSCRIPTION;
	}
}
