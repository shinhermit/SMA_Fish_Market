package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.FishSellerBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitFirstBidBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private FishSellerBehaviour myFSM;
	
	/** Allows filtering incoming messages. */
	private final MessageTemplate messageFilter;
	
	/** The selected transition to the next state. */
	private int transition =
			FishSellerBehaviour.TRANSITION_TO_WAIT_SECOND_BID;
	
	/** The time to wait for the first bid. */
	private static final long FIRST_BID_WAIT_DURATION = 20000l; // 20 sec
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitFirstBidBehaviour(
			SellerAgent mySellerAgent,
			FishSellerBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
		
		this.messageFilter =
				this.createMessageFilter();
	}
	
	@Override
	public void action()
	{
		this.block(FIRST_BID_WAIT_DURATION);
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				this.messageFilter);
		
		SellerAgent mySellerAgent =
				(SellerAgent)super.myAgent;
		
		if(mess != null)
		{
			this.transition =
					FishSellerBehaviour.TRANSITION_TO_WAIT_SECOND_BID;
		}
		else
		{
			float newStep = mySellerAgent.getPriceStep() / 2f;
			float newPrice = mySellerAgent.getCurrentPrice() - newStep;
			
			if(newPrice >= mySellerAgent.getMinPrice()
					&& newStep >= mySellerAgent.getMinPriceStep())
			{
				mySellerAgent.decreasePriceStep();
				mySellerAgent.decreasePrice();
				
				this.transition =
						FishSellerBehaviour.TRANSITION_TO_ANNOUNCE;
			}
			else
			{
				this.transition =
						FishSellerBehaviour.TRANSITION_TO_TERMINATE_CANCEL;
			}
		}
	}
	
	@Override
	public int onEnd()
	{
		return this.transition;
	}
	
	/**
	 * 
	 * @return the message filter to use in this behaviour.
	 */
	private MessageTemplate createMessageFilter()
	{
		return MessageTemplate.and(
				this.myFSM.getMessageFilter(),
				MessageTemplate.MatchPerformative(
						FishMarket.Performatives.TO_BID));
	}
}
