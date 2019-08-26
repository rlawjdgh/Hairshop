package vo;

public class ReservationVO {
	
	private int reservation_idx;
	private int login_idx;
	private int staff_idx;
	private String cal_day;
	private String getTime;
	private String surgery_name;
	private int price;
	private int complete;
	
	 
	public int getReservation_idx() {
		return reservation_idx;
	}
	public void setReservation_idx(int reservation_idx) {
		this.reservation_idx = reservation_idx;
	}
	public int getLogin_idx() {
		return login_idx;
	}
	public void setLogin_idx(int login_idx) {
		this.login_idx = login_idx;
	}
	public int getStaff_idx() {
		return staff_idx;
	}
	public void setStaff_idx(int staff_idx) {
		this.staff_idx = staff_idx;
	}
	public String getCal_day() {
		return cal_day;
	}
	public void setCal_day(String cal_day) {
		this.cal_day = cal_day;
	}
	public String getGetTime() {
		return getTime;
	}
	public void setGetTime(String getTime) {
		this.getTime = getTime;
	}
	public String getSurgery_name() {
		return surgery_name;
	}
	public void setSurgery_name(String surgery_name) {
		this.surgery_name = surgery_name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getComplete() {
		return complete;
	}
	public void setComplete(int complete) {
		this.complete = complete;
	}
	
	

}
