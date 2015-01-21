package fr.univpau.m2ti.sma.fishmarket.message;

import jade.lang.acl.ACLMessage;

/**
 * Defines the constants needed by the fish market protocol.
 * 
 * @author Josuah Aron
 *
 */
public class FishMarket
{
	public static class Performatives
	{
		/** Requests of the registration of an auction, from a seller agent to the market agent. */
		public static final int TO_REGISTER = ACLMessage.REQUEST;
		
		/** Requests of the subscription to an auction, from a bidder agent to the market agent. */
		public static final int TO_SUBSCRIBE = ACLMessage.SUBSCRIBE;
		
		/** Rejects the registration of an auction or the subscription of a bidder to an auction, from the market agent to other agents. */
		public static final int TO_REFUSE = ACLMessage.REFUSE;
		
		/** Confirms the registration of an auction or the subscription of a bidder to an auction, from the market agent to other agents. */
		public static final int TO_ACCEPT = ACLMessage.CONFIRM;
		
		/** Requests the list of available auction, from a bidder agent to the market agent. */
		public static final int TO_REQUEST = ACLMessage.REQUEST;
		
		/** Provides the list of available auction, from the market agent to a bidder. */
		public static final int TO_PROVIDE = ACLMessage.INFORM;
		
		/** Announces a price for a fish supply, from a seller agent to the market agent, or the market agent to a bidder agent. */
		public static final int TO_ANNOUNCE = ACLMessage.CFP;
		
		/** Places a buy proposal on a price announcement, from a bidder agent to the market agent or the market agent to a seller agent. */
		public static final int TO_BID = ACLMessage.PROPOSE;
		
		/** Attributes a fish supply to a bidder agent, from a seller agent to the market agent, or the market agent to a bidder agent. */
		public static final int TO_ATTRIBUTE = ACLMessage.ACCEPT_PROPOSAL;
		
		/** Rejects a bid of a bidder, from a seller agent to the market agent or the market agent to a bidder agent. */
		public static final int REP_BID = ACLMessage.INFORM;
		
		/** Provides a fish supply to a bidder agent, from a seller agent to the market agent or the market agent to a bidder agent. */
		public static final int TO_GIVE = ACLMessage.AGREE;
		
		/** Provides the payment to the seller agent, from a bidder agent to the market agent or the market agent to a seller agent. */
		public static final int TO_PAY = ACLMessage.CONFIRM;
		
		/** Informs the end of the auction. */
		public static final int AUCTION_OVER = ACLMessage.CANCEL;
		
		/** Informs that the auction has been cancelled (ended without attribution). */
		public static final int TO_CANCEL = ACLMessage.FAILURE;
	}
	
	public static class Topics
	{
		/** The topic of the conversations of the sellers with the fish market outside auction. */
		public static final String TOPIC_AUCTION_REGISTRATION = 
				FishMarket.Topics.class.getName()+":TOPIC_AUCTION_REGISTRATION";
		
		/** The topic of the conversations of the bidders with the fish market outside auction. */
		public static final String TOPIC_BIDDERS_SUBSCRIPTION = 
				FishMarket.Topics.class.getName()+":TOPIC_BIDDERS_SUBSCRIPTION";

		/** The topic of the conversations of the bidders with the fish market outside auction. */
		public static final String TOPIC_AUCTION_MANAGEMENT = 
				FishMarket.Topics.class.getName()+":TOPIC_RUNNING_AUCTION";
	}
}
