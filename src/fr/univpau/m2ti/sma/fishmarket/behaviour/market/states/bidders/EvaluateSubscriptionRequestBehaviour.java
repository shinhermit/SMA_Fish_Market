package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

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
	private int status = BidderManagementBehaviour.STATUS_REFUSE_NOT_UNDERSTOOD;
	
	/** Tells whether the subscription has been accepted or not. */
	private boolean subscriptionAccepted;
	
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
		
		ACLMessage request = this.myFSM.getRequest();
		
		MarketAgent myMarketAgent = (MarketAgent)
				super.myAgent;
		
		String auctionId = (String) request.getContent();
		
		if(auctionId != null)
		{
			Auction requestedAuction =
					myMarketAgent.findAuction(auctionId);
			
			if(requestedAuction != null)
			{
				if(requestedAuction.getStatus() != Auction.STATUS_OVER
						&& requestedAuction.getStatus() != Auction.STATUS_CANCELLED
						&& !myMarketAgent.isSuscriber(
								auctionId, request.getSender()))
				{
					this.subscriptionAccepted = true;  //// Accepted !!!!!!!!!!
					
					ACLMessage reply = request.createReply();
					AID bidderAID = request.getSender();
					
					// Register subscription
					
					myMarketAgent.addSuscriber(auctionId, bidderAID);
					
					// Reply accept
					reply.setPerformative(
							FishMarket.Performatives.TO_ACCEPT);
					
					reply.setConversationId(auctionId);		
					
					myMarketAgent.send(reply);
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
	
	@Override
	public int onEnd()
	{
		return (this.subscriptionAccepted) ?
				BidderManagementBehaviour.TRANSITION_TO_NOTIFY_SELLER :
					this.replyRefuse();
	}
	
	/**
	 * Replies to the request by a refuse.
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
		
		reply.setContent(
				String.valueOf(this.status));
		
		super.myAgent.send(reply);
		
		return BidderManagementBehaviour.TRANSITION_TO_WAIT_REQUEST;
	}
}
