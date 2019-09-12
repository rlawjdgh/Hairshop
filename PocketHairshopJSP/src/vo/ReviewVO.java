package vo;

public class ReviewVO {
	
	private int review_idx;
	private int login_idx;
	private int reservation_idx;
	private int store_idx;
	private String staff_name;
	private String context;
	private int rating;
	private String regdate;
	 
	public int getReview_idx() {
		return review_idx;
	}
	public void setReview_idx(int review_idx) {
		this.review_idx = review_idx;
	}
	public int getLogin_idx() {
		return login_idx;
	}
	public void setLogin_idx(int login_idx) {
		this.login_idx = login_idx;
	}
	public int getReservation_idx() {
		return reservation_idx;
	}
	public void setReservation_idx(int reservation_idx) {
		this.reservation_idx = reservation_idx;
	}
	public int getStore_idx() {
		return store_idx;
	}
	public void setStore_idx(int store_idx) {
		this.store_idx = store_idx;
	}
	public String getStaff_name() {
		return staff_name;
	}
	public void setStaff_name(String staff_name) {
		this.staff_name = staff_name;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getRegdate() {
		return regdate;
	}
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	
	

}
