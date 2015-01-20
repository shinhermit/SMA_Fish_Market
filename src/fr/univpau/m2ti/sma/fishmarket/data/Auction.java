package fr.univpau.m2ti.sma.fishmarket.data;

import jade.core.AID;
import jade.util.leap.Serializable;

@SuppressWarnings("serial")
/**
 * Represents an auction.
 * 
 * <p>An auction is associated with one and only one seller agent, who instantiated the auction, and possibly several bidder agents.</p>
 * 
 * @author Josuah Aron
 *
 */
public class Auction implements Serializable
{
	/** The agent ID of the seller who created this auction. */
	private AID sellerID;
	
	/** The status of the auction. One of the static fields prefixed with <i>STATUS_</i>.*/
	private int status;
	
	/** Status code which indicates that an auction has been created, but the seller has made no announcement yet. */
	public static final int STATUS_CREATED;
	
	/** Status code which indicates that an auction has ended. */
	public static final int STATUS_RUNNING;
	
	/** Status code which indicates that an auction has ended. */
	public static final int STATUS_OVER;
	
	/** Status code which indicates that an auction has ended. */
	public static final int STATUS_CANCELLED;
	
	static
	{
		int start = -1;
		
		STATUS_CREATED = ++start;
		STATUS_RUNNING = ++start;
		STATUS_OVER = ++start;
		STATUS_CANCELLED = ++start;
	}

	/**
	 * Creates a new auction, associated with it seller initiator.
	 * 
	 * @param sellerID the seller agent who requested the creation of this auction.
	 */
	public Auction(AID sellerID)
	{
		this.sellerID = sellerID;
		
		this.status = Auction.STATUS_CREATED;
	}
	
	/**
	 * Default constructor.
	 */
	public Auction()
	{
		this(null);
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean equals = false;
		
		if(o != null)
		{
			if(o instanceof Auction)
			{
				Auction other = (Auction) o;
				
				equals = this.sellerID.equals(
						other.getSellerID());
			}
		}
		
		return equals;
	}
	
	@Override
	public String toString()
	{
		return
				"Auction {seller: " +
				this.sellerID.toString() +
				"; status: " +
				Auction.printStatus(this.status) +
				"}";
	}
	
	@Override
	public int hashCode()
	{
		return this.toString().hashCode();
	}

	/**
	 * 
	 * @return the seller agent who requested the creation of this auction.
	 */
	public AID getSellerID()
	{
		return sellerID;
	}

	/**
	 * 
	 * @param sellerID the seller agent who requested the creation of this auction.
	 */
	public void setSellerID(AID sellerID)
	{
		this.sellerID = sellerID;
	}

	/**
	 * 
	 * @return the status of the auction. One of the static fields prefixed with <i>STATUS_</i>.
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 * Forces the status of the auction.
	 * 
	 * @param status the status of the auction. One of the static fields prefixed with <i>STATUS_</i>.
	 */
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	/**
	 * Converts an auction status code into a human readable string.
	 * 
	 * @param status the auction status code.
	 * 
	 * @return the corresponding string.
	 */
	public static String printStatus(int status)
	{
		String statusString = "UNKOWN";
		
		if(status == Auction.STATUS_CREATED)
		{
			statusString = "CREATED";
		}
		else if(status == Auction.STATUS_RUNNING)
		{
			statusString = "RUNNING";
			
		}
		else if(status == Auction.STATUS_OVER)
		{
			statusString = "OVER";
			
		}
		else if(status == Auction.STATUS_CANCELLED)
		{
			statusString = "CANCELLED";
			
		}
		
		return statusString;
	}
}
