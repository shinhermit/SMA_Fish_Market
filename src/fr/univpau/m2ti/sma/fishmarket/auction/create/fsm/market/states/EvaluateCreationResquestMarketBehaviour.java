package fr.univpau.m2ti.sma.fishmarket.auction.create.fsm.market.states;

import java.util.Scanner;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.create.fsm.CreateAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class EvaluateCreationResquestMarketBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private CreateAuctionMarketFSMBehaviour myFSM;
	
	/** The next selected transition. */
	private int transition;
	
	/**
	 * Creates a behaviour which is to be associated with a state of the 
	 * market agent's FSM behaviour.
	 * 
	 * @param myMarketAgent
	 * 			the market agent of which FSM behavior's state this behaviour is to be associated.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public EvaluateCreationResquestMarketBehaviour(
			MarketAgent myMarketAgent,
			CreateAuctionMarketFSMBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		MarketAgent myMarketAgent = (MarketAgent)myAgent;
		
		ACLMessage request = this.myFSM.getRequest();
		
		String auctionId =
				RunningAuctionMarketFSMBehaviour.createAuctionId(
						request.getSender());
		
		// DEBUG
		System.out.println("Market: evaluating creation request of auction with id "+auctionId);
		System.out.println("Topic is: "+CreateAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
		
		boolean accepted = true;
		
		Auction auction = myMarketAgent.findAuction(auctionId);
		
		if(auction == null)
		{
			auction = new Auction(auctionId);
		}
		else if(auction.getStatus() != Auction.STATUS_CANCELLED
				&& auction.getStatus() != Auction.STATUS_OVER)
		{
			accepted = false;
		}
		
		if(accepted)
		{
			// DEBUG
			System.out.println("Market: auction is not yet registered");
			
			Scanner input = new Scanner(request.getContent());
			input.useDelimiter(":");
			
			String fishSupplyName = input.hasNext() ? input.next() : "";
			float price = input.hasNextFloat() ? input.nextFloat() : 0f;
			
			input.close();
			
			// Register auction
			auction.setStatus(
					Auction.STATUS_RUNNING);
			
			auction.setAuctionName(fishSupplyName);
			
			auction.setCurrentPrice(price);
			
			myMarketAgent.registerAuction(
					auction, request.getSender());
			
			request.setConversationId(auctionId);
			
			// Next transition
			this.transition =
					CreateAuctionMarketFSMBehaviour.TRANSITION_TO_CONFIRM_CREATION;
		}
		else
		{
			// DEBUG
			System.out.println("Market: auction is already registered");
			
			// Reply refuse
			ACLMessage reply =
					this.myFSM.getRequest().createReply();
			
			reply.setPerformative(
					FishMarket.Performatives.TO_REFUSE);
			
			// Set topic
			reply.addReceiver(
					CreateAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
			
			super.myAgent.send(reply);
			
			// Next transition
			this.transition =
					CreateAuctionMarketFSMBehaviour.TRANSITION_TO_WAIT_REQUEST;
			
			this.myFSM.setRequest(null);
		}
	}
	
	@Override
	public int onEnd()
	{
		return this.transition;
	}
}