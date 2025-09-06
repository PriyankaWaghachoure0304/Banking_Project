package com.sdp.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Captcha;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;

import com.sdp.dao.AccountDAO;
import com.sdp.model.AddressDocuments;
import com.sdp.model.PersonalDetails;

public class AccountController extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;


	@Wire
	private Textbox firstNameBox;
	@Wire private Textbox  middleNameBox;
	@Wire private Textbox  lastNameBox;
	@Wire private Textbox  relativeNameBox;
	@Wire private Textbox  cityOfBirthBox;
	@Wire private Textbox  incomeBox;

	@Wire
	private Textbox Occupation;
	@Wire private Textbox  Idnumber;
	@Wire private Textbox  email;
	@Wire private Textbox  captchaInput;
	@Wire private Textbox  addressBox;
	@Wire private Textbox  peraddressBox;

	@Wire
	private Checkbox chk1;
	@Wire private Checkbox  chk2;
	@Wire private Checkbox  creditCard;
	@Wire private Checkbox  india;
	@Wire private Checkbox  other;
	@Wire private Checkbox  declaration;
	@Wire private Checkbox  sameAsCurrent;

	@Wire
	private Datebox dobBox;

	@Wire
	private Radiogroup genderGroup;

	@Wire
	private Combobox idProofBox;
	@Wire private Combobox  countryBox;

	@Wire
	private Longbox mobileNumber;

	@Wire
	private Captcha cpa;

	@Wire
	private Intbox pin;
	@Wire private Intbox  perpin;

	@Wire
	private Combobox percity;
	@Wire private Combobox  perdistrict;
	@Wire private Combobox  perstate;
	@Wire private Combobox  percountry;

	@Wire
	private Combobox country;
	@Wire private Combobox  stateBox;
	@Wire private Combobox  districtBox;
	@Wire private Combobox  cityBox;

	@Wire
	private Button uploadBtn;

	@Wire
	private Div displayDiv;
	@Wire private Div  aadharDiv;
	@Wire private Div  panDiv;

	@Wire
	Tabbox tabbox1;

	@Wire
	Tab tab1;
	@Wire Tab  tab2;
	@Wire Tab  tab3;
	@Wire Tab  tab4;
	@Wire Tab  tab7;
	@Wire Tab  tab5;
	@Wire Tab  tab6;

	@Wire
	Tabpanel tab1panel;
	@Wire Tabpanel  tab2panel;
	@Wire Tabpanel  tab3panel;
	@Wire Tabpanel  tab4panel;
	@Wire Tabpanel  tab5panel;
	@Wire Tabpanel  tab6panel;
	@Wire Tabpanel  tab7panel;

	@Wire
	Label lblerror1;
	@Wire Label  lblerror2;
	@Wire Label  lblerror3;
	@Wire Label  lblerror4;
	@Wire Label  lblerror5;
	@Wire Label  lblerror6;
	@Wire Label  lblerror7;

	@Wire
	private Label reviewName;
	@Wire private Label  reviewDob;
	@Wire private Label  reviewGender;
	@Wire private Label  reviewMobile;
	@Wire private Label  reviewEmail;
	@Wire private Label  reviewAddress;
	@Wire private Label  reviewAccountType;



	private byte[] photoBytes;

	private byte[] aadharBytes;

	private byte[] panBytes;



	private final Map<String, List<String>> countryStateMap = new HashMap<>();

	private final Map<String, List<String>> stateDistrictMap = new HashMap<>();

	private final Map<String, List<String>> districtCityMap = new HashMap<>();

	private final Map<String, String> stateCountryMap = new HashMap<>();

	private final Map<String, String> districtStateMap = new HashMap<>();

	private final Map<String, String> cityDistrictMap = new HashMap<>();

	private boolean isPopulating = false;

transient	AccountDAO dao = new AccountDAO();
static final String VERROR="Validation Error";

	@Override

	public void doAfterCompose(Div comp) throws Exception {

		super.doAfterCompose(comp);

		
		dobBox.setConstraint((Constraint) (comp1, value) -> {

			if (value == null) {

				throw new WrongValueException(dobBox, "Date of birth is required");

			}

			Calendar selected = Calendar.getInstance();

			selected.setTime((Date) value);

			selected.set(Calendar.HOUR_OF_DAY, 0);

			selected.set(Calendar.MINUTE, 0);

			selected.set(Calendar.SECOND, 0);

			selected.set(Calendar.MILLISECOND, 0);

			Calendar today = Calendar.getInstance();

			today.set(Calendar.HOUR_OF_DAY, 0);

			today.set(Calendar.MINUTE, 0);

			today.set(Calendar.SECOND, 0);

			today.set(Calendar.MILLISECOND, 0);

			if (selected.after(today)) {

				throw new WrongValueException(dobBox, "Date cannot be in the future");

			}

		});

	

		countryStateMap.put("India", Arrays.asList("Maharashtra", "Karnataka"));

		countryStateMap.put("USA", Arrays.asList("California", "Texas"));

		stateDistrictMap.put("Maharashtra", Arrays.asList("Pune", "Nagpur"));

		stateDistrictMap.put("Karnataka", Arrays.asList("Bengaluru", "Mysuru"));

		stateDistrictMap.put("California", Arrays.asList("Los Angeles County", "San Diego County"));

		stateDistrictMap.put("Texas", Arrays.asList("Dallas County", "Harris County"));

		districtCityMap.put("Pune", Arrays.asList("Shivajinagar", "Hinjewadi", "Kothrud"));

		districtCityMap.put("Nagpur", Arrays.asList("Sitabuldi", "Dharampeth"));

		districtCityMap.put("Bengaluru", Arrays.asList("Whitefield", "BTM Layout"));

		districtCityMap.put("Mysuru", Arrays.asList("Vijayanagar", "Jayanagar"));

		districtCityMap.put("Los Angeles County", Arrays.asList("Los Angeles", "Beverly Hills"));

		districtCityMap.put("San Diego County", Arrays.asList("San Diego", "La Jolla"));

		districtCityMap.put("Dallas County", Arrays.asList("Dallas", "Irving"));

		districtCityMap.put("Harris County", Arrays.asList("Houston", "Pasadena"));

		for (Map.Entry<String, List<String>> e : countryStateMap.entrySet()) {

			for (String s : e.getValue())
				stateCountryMap.put(s, e.getKey());

		}

		for (Map.Entry<String, List<String>> e : stateDistrictMap.entrySet()) {

			for (String d : e.getValue())
				districtStateMap.put(d, e.getKey());

		}

		for (Map.Entry<String, List<String>> e : districtCityMap.entrySet()) {

			for (String ci : e.getValue())
				cityDistrictMap.put(ci, e.getKey());

		}

		populateComboBox(country, countryStateMap.keySet());

		populateComboBox(stateBox, flattenList(countryStateMap.values()));

		populateComboBox(districtBox, flattenList(stateDistrictMap.values()));

		populateComboBox(cityBox, flattenList(districtCityMap.values()));

	}

	

	private void populateComboBox(Combobox box, Collection<String> items) {

		box.getItems().clear();

		for (String s : items) {

			if (!itemExists(box, s))
				box.appendItem(s);

		}

	}

	private boolean itemExists(Combobox box, String label) {

		for (Comboitem item : box.getItems()) {

			if (item.getLabel().equals(label))
				return true;

		}

		return false;

	}

	private List<String> flattenList(Collection<List<String>> lists) {

		List<String> result = new ArrayList<>();

		for (List<String> l : lists)
			result.addAll(l);

		return result;

	}



	private boolean isEmpty(Textbox box) {
		return box == null || box.getValue() == null || box.getValue().trim().isEmpty();
	}

	private boolean isEmpty(Combobox box) {

		if (box == null)
			return true;

		if (box.getSelectedItem() != null)
			return false;

		String v = box.getValue();

		return v == null || v.trim().isEmpty();

	}

	private boolean isEmpty(Intbox box) {
		return box == null || box.getValue() == null;
	}

	private boolean isEmpty(Longbox box) {
		return box == null || box.getValue() == null;
	}

	private boolean isEmpty(Datebox box) {
		return box == null || box.getValue() == null;
	}

	private boolean isEmpty(Radiogroup group) {
		return group == null || group.getSelectedItem() == null;
	}

	private void showError(Label errorLabel, boolean visible, String message) {

		errorLabel.setVisible(visible);

		if (visible) {

			errorLabel.setStyle("color:red");

			errorLabel.setValue(message != null ? message : "");

		} else {

			errorLabel.setValue("");

		}

	}

	private void goNextTab(Tab nextTab) {
		tabbox1.setSelectedTab(nextTab);
	}

	

	@Listen("onChange = #country")

	public void countryChanged() {

		if (isPopulating)
			return;

		isPopulating = true;

		String selectedCountry = country.getValue();

		stateBox.setValue("");
		districtBox.setValue("");
		cityBox.setValue("");

		stateBox.getItems().clear();
		districtBox.getItems().clear();
		cityBox.getItems().clear();

		if (countryStateMap.containsKey(selectedCountry))
			populateComboBox(stateBox, countryStateMap.get(selectedCountry));

		isPopulating = false;

	}

	@Listen("onChange = #stateBox")

	public void stateChanged() {

		if (isPopulating)
			return;

		isPopulating = true;

		String state = stateBox.getValue();

		String countryName = stateCountryMap.get(state);

		if (countryName != null)
			country.setValue(countryName);

		districtBox.setValue("");
		cityBox.setValue("");

		districtBox.getItems().clear();
		cityBox.getItems().clear();

		if (stateDistrictMap.containsKey(state))
			populateComboBox(districtBox, stateDistrictMap.get(state));

		isPopulating = false;

	}

	@Listen("onChange = #districtBox")

	public void districtChanged() {

		if (isPopulating)
			return;

		isPopulating = true;

		String district = districtBox.getValue();

		String state = districtStateMap.get(district);

		String countryName = stateCountryMap.get(state);

		if (state != null)
			stateBox.setValue(state);

		if (countryName != null)
			country.setValue(countryName);

		cityBox.setValue("");

		cityBox.getItems().clear();

		if (districtCityMap.containsKey(district))
			populateComboBox(cityBox, districtCityMap.get(district));

		isPopulating = false;

	}

	@Listen("onChange = #cityBox")

	public void cityChanged() {

		if (isPopulating)
			return;

		isPopulating = true;

		String city = cityBox.getValue();

		if (city == null || city.isEmpty()) {
			isPopulating = false;
			return;
		}

		String district = cityDistrictMap.get(city);

		String state = districtStateMap.get(district);

		String countryName = stateCountryMap.get(state);

		country.setValue(countryName);

		stateBox.getItems().clear();

		populateComboBox(stateBox, countryStateMap.get(countryName));

		stateBox.setValue(state);

		districtBox.getItems().clear();

		populateComboBox(districtBox, stateDistrictMap.get(state));

		districtBox.setValue(district);

		cityBox.getItems().clear();

		populateComboBox(cityBox, districtCityMap.get(district));

		cityBox.setValue(city);

		isPopulating = false;

	}

	

	@Listen("onCheck=#sameAsCurrent")

	public void copyCurrentAddress() {

		if (sameAsCurrent.isChecked()) {

			peraddressBox.setValue(addressBox.getValue());

			percity.setValue(cityBox.getValue());

			perdistrict.setValue(districtBox.getValue());

			perstate.setValue(stateBox.getValue());

			perpin.setValue(pin.getValue());

			percountry.setValue(country.getValue());

			peraddressBox.setReadonly(true);

			percity.setReadonly(true);

			perdistrict.setReadonly(true);

			perstate.setReadonly(true);

			perpin.setReadonly(true);

			percountry.setReadonly(true);

		} else {

			peraddressBox.setReadonly(false);

			percity.setReadonly(false);

			perdistrict.setReadonly(false);

			perstate.setReadonly(false);

			perpin.setReadonly(false);

			percountry.setReadonly(false);

			peraddressBox.setValue("");

			percity.setValue("");

			perdistrict.setValue("");

			perstate.setValue("");

			perpin.setValue(null);

			percountry.setValue("");

		}

	}


	private boolean validateIdNumber() {

		String selectedProof = idProofBox.getValue();

		String idNumber = Idnumber.getValue();

		if (selectedProof == null || selectedProof.trim().isEmpty()) {

			Messagebox.show("Please select an ID proof type first.");

			idProofBox.setFocus(true);

			return false;

		}

		if (idNumber == null || idNumber.trim().isEmpty()) {

			Messagebox.show("Please enter ID number.");

			Idnumber.setFocus(true);

			return false;

		}

		String regex;

		String errorMsg;

		switch (selectedProof) {

		case "Aadhar Card":

			regex = "^[0-9]{12}$";

			errorMsg = "Aadhaar must be exactly 12 digits.";

			break;

		case "PAN Card":

			regex = "^[A-Z]{5}[0-9]{4}[A-Z]$";

			errorMsg = "PAN must be 10 chars (AAAAA9999A).";

			break;

		case "Voter ID":

			regex = "^[A-Z]{3}[0-9]{7}$";

			errorMsg = "Voter ID must be 10 chars (AAA9999999).";

			break;

		case "Passport":

			regex = "^[A-Z][0-9]{7}$";

			errorMsg = "Passport must be 8 chars (A9999999).";

			break;

		default:

			Messagebox.show("Unknown ID proof type.");

			return false;

		}

		if (!idNumber.toUpperCase().matches(regex)) {

			Messagebox.show(errorMsg, VERROR, Messagebox.OK, Messagebox.EXCLAMATION);

			Idnumber.setFocus(true);

			return false;

		}

		return true;

	}

	private boolean validateMobile() {

		Long value = mobileNumber.getValue();

		if (value == null) {

			Messagebox.show("Mobile number is required.", VERROR, Messagebox.OK, Messagebox.EXCLAMATION);

			mobileNumber.setFocus(true);

			return false;

		}

		String val = String.valueOf(value);

		if (!val.matches("^[6-9]\\d{9}$")) {

			Messagebox.show("Enter a valid Indian mobile number.", VERROR, Messagebox.OK,
					Messagebox.EXCLAMATION);

			mobileNumber.setFocus(true);

			return false;

		}

		if (!email.getValue().matches("^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.in|outlook\\.org)$")) {

			Messagebox.show("Enter a valid email address.",VERROR, Messagebox.OK, Messagebox.EXCLAMATION);

			mobileNumber.setFocus(true);

			return false;

		}

		return true;

	}


	private static final String ERROR = "Please fill all required fields in this tab!";

	@Listen("onClick = #accounselectionnext")

	public void goFromTab1ToTab2() {

		try {

			if (!chk1.isChecked() && !chk2.isChecked()) {

				showError(lblerror1, true, "Please select one account type.");

				return;

			} else if (chk1.isChecked() && chk2.isChecked()) {

				showError(lblerror1, true, "Please select only one account type.");

				return;

			}

			showError(lblerror1, false, null);

			goNextTab(tab2);

		} catch (WrongValueException e) {

			showError(lblerror1, true, ERROR);

		}

	}

	@Listen("onClick = #personaldetail")

	public void goFromTab2ToTab3() {

		try {

			if (isEmpty(firstNameBox) || isEmpty(lastNameBox) || isEmpty(dobBox) || isEmpty(genderGroup)

					|| (!india.isChecked() && !other.isChecked())) {

				throw new WrongValueException("Fill required fields on Personal Details.");

			}


			if (!firstNameBox.getValue().matches("^[A-Za-z ]+$")) {

				Messagebox.show("First Name must contain only letters.", VERROR, Messagebox.OK,
						Messagebox.EXCLAMATION);

				firstNameBox.setFocus(true);

				return;

			}

			if (!isEmpty(middleNameBox) && !middleNameBox.getValue().matches("^[A-Za-z ]+$")) {

				Messagebox.show("Middle Name must contain only letters.", VERROR, Messagebox.OK,
						Messagebox.EXCLAMATION);

				middleNameBox.setFocus(true);

				return;

			}

			if (!lastNameBox.getValue().matches("^[A-Za-z ]+$")) {

				Messagebox.show("Last Name must contain only letters.", VERROR, Messagebox.OK,
						Messagebox.EXCLAMATION);

				lastNameBox.setFocus(true);

				return;

			}

			if (!relativeNameBox.getValue().matches("^[A-Za-z ]+$")) {

				Messagebox.show("Guardian Name must contain only letters.", VERROR, Messagebox.OK,
						Messagebox.EXCLAMATION);

				relativeNameBox.setFocus(true);

				return;

			}

			if (!cityOfBirthBox.getValue().matches("^[A-Za-z ]+$")) {

				Messagebox.show("City of Birth must contain only letters.", VERROR, Messagebox.OK,
						Messagebox.EXCLAMATION);

				cityOfBirthBox.setFocus(true);

				return;

			}


			if (!validateIdNumber())
				return;

			

			String occupationVal = Occupation.getValue();

			if (occupationVal == null || occupationVal.trim().isEmpty()) {

				Messagebox.show("Occupation is required.", VERROR, Messagebox.OK, Messagebox.EXCLAMATION);

				Occupation.setFocus(true);

				return;

			}

			if (occupationVal.trim().matches("\\d+")) {

				Messagebox.show("Occupation cannot be numeric.", VERROR, Messagebox.OK,
						Messagebox.EXCLAMATION);

				Occupation.setFocus(true);

				return;

			}


			String incomeVal = incomeBox.getValue();

			if (incomeVal == null || incomeVal.trim().isEmpty()) {

				Messagebox.show("Annual Income is required.", VERROR, Messagebox.OK,
						Messagebox.EXCLAMATION);

				incomeBox.setFocus(true);

				return;

			}

			if (!incomeVal.trim().matches("\\d+")) {

				Messagebox.show("Annual Income must contain digits only.", VERROR, Messagebox.OK,
						Messagebox.EXCLAMATION);

				incomeBox.setFocus(true);

				return;

			}

			showError(lblerror2, false, null);

			goNextTab(tab3);

		} catch (WrongValueException e) {

			showError(lblerror2, true, ERROR);

		}

	}

	@Listen("onClick = #contactdetail")

	public void goFromTab3ToTab4() {

		try {

			if (isEmpty(email) || isEmpty(countryBox) || isEmpty(mobileNumber)) {

				throw new WrongValueException("Fill required fields on Contact Details.");

			}


			if (!validateMobile())
				return;

			showError(lblerror3, false, null);

			goNextTab(tab4);

		} catch (WrongValueException e) {

			showError(lblerror3, true, ERROR);

		}

	}

	@Listen("onClick = #addressdetailnext")

	public void goFromTab4ToTab5() {

		try {

			if (isEmpty(addressBox) || isEmpty(cityBox) || isEmpty(districtBox) || isEmpty(stateBox) || isEmpty(pin)
					|| isEmpty(country)

					|| isEmpty(peraddressBox) || isEmpty(percity) || isEmpty(perdistrict) || isEmpty(perstate)
					|| isEmpty(perpin) || isEmpty(percountry)) {

				throw new WrongValueException("Fill required fields on Address Details.");

			}

			showError(lblerror4, false, null);

			goNextTab(tab5);

		} catch (WrongValueException e) {

			showError(lblerror4, true, ERROR);

		}

	}

	@Listen("onClick = #uploaddoc")

	public void goFromTab5ToTab6() {

		try {
			

			if (photoBytes == null) {
				showError(lblerror5, true, "Please upload your Photo!");
				return;
			}

			if (aadharBytes == null) {
				showError(lblerror5, true, "Please upload your Aadhar PDF!");
				return;
			}

			if (panBytes == null) {
				showError(lblerror5, true, "Please upload your PAN PDF!");
				return;
			}

			showError(lblerror5, false, null);
			populateReview();
			goNextTab(tab6);

		} catch (WrongValueException e) {

			showError(lblerror5, true, ERROR);

		}

	}

	@Listen("onClick = #fromreview")

	public void goFromTab6ToTab7() {

		try {


			if (!declaration.isChecked())
				throw new WrongValueException("Please agree to the declaration.");

			if (captchaInput.getValue() == null || !captchaInput.getValue().equals(cpa.getValue()))

				throw new WrongValueException("Captcha does not match.");

			showError(lblerror6, false, null);

			goNextTab(tab7);

		} catch (WrongValueException e) {

			showError(lblerror6, true, e.getMessage());

		}

	}


	@Listen("onUpload = #uploadBtn")

	public void uploadImage(UploadEvent event) {

		Media media = event.getMedia();

		if (media instanceof org.zkoss.image.Image) {

			photoBytes = media.getByteData();

			displayDiv.getChildren().clear();

			org.zkoss.zul.Image img = new org.zkoss.zul.Image();

			img.setContent((org.zkoss.image.Image) media);

			img.setWidth("150px");

			img.setHeight("150px");

			img.setParent(displayDiv);

		} else {

			Messagebox.show("Not an image", "Error", Messagebox.OK, Messagebox.EXCLAMATION);

		}

	}

	@Listen("onUpload = #uploadBtn1")

	public void uploadAadhar(UploadEvent event) {

		Media media = event.getMedia();

		if (media != null && "application/pdf".equalsIgnoreCase(media.getContentType())) {

			aadharBytes = media.getByteData();

			aadharDiv.getChildren().clear();

			new Label(media.getName()).setParent(aadharDiv);

		} else {

			Messagebox.show("Invalid file. Please upload a PDF.", "Error", Messagebox.OK, Messagebox.EXCLAMATION);

		}

	}

	@Listen("onUpload = #uploadBtn2")

	public void uploadPan(UploadEvent event) {

		Media media = event.getMedia();

		if (media != null && "application/pdf".equalsIgnoreCase(media.getContentType())) {

			panBytes = media.getByteData();

			panDiv.getChildren().clear();

			new Label(media.getName()).setParent(panDiv);

		} else {

			Messagebox.show("Invalid file. Please upload a PDF.", "Error", Messagebox.OK, Messagebox.EXCLAMATION);

		}

	}

	public byte[] getFileBytes() {
		return photoBytes;
	}



	@Listen("onClick = #submit")

	public void onSubmit() throws SQLException   {

		List<String> errors = new ArrayList<>();

		// Account selection

		if (chk1.isChecked() == chk2.isChecked()) {

			errors.add("Select exactly one account type (Savings or Current).");

		}

	

		if (isEmpty(firstNameBox))
			errors.add("First name is required.");

		if (isEmpty(lastNameBox))
			errors.add("Last name is required.");

		if (isEmpty(dobBox))
			errors.add("Date of birth is required.");

		if (isEmpty(genderGroup))
			errors.add("Gender is required.");

		if (!india.isChecked() && !other.isChecked())
			errors.add("Nationality is required.");

		if (isEmpty(relativeNameBox))
			errors.add("Guardian/Relative name is required.");

		if (isEmpty(cityOfBirthBox))
			errors.add("City of birth is required.");

		if (isEmpty(incomeBox))
			errors.add("Annual income is required.");

		if (isEmpty(Occupation))
			errors.add("Occupation is required.");


		if (isEmpty(idProofBox))
			errors.add("Select an ID proof type.");

		if (isEmpty(Idnumber))
			errors.add("Enter the ID number for the selected proof.");


		if (isEmpty(countryBox))
			errors.add("Select a country code.");

		if (isEmpty(mobileNumber))
			errors.add("Mobile number is required.");

		if (isEmpty(email))
			errors.add("Email is required.");


		if (isEmpty(addressBox))
			errors.add("Current address is required.");

		if (isEmpty(cityBox))
			errors.add("Current city is required.");

		if (isEmpty(districtBox))
			errors.add("Current district is required.");

		if (isEmpty(stateBox))
			errors.add("Current state is required.");

		if (isEmpty(pin))
			errors.add("Current PIN is required.");

		if (isEmpty(country))
			errors.add("Current country is required.");


		if (isEmpty(peraddressBox))
			errors.add("Permanent address is required.");

		if (isEmpty(percity))
			errors.add("Permanent city is required.");

		if (isEmpty(perdistrict))
			errors.add("Permanent district is required.");

		if (isEmpty(perstate))
			errors.add("Permanent state is required.");

		if (isEmpty(perpin))
			errors.add("Permanent PIN is required.");

		if (isEmpty(percountry))
			errors.add("Permanent country is required.");


		if (photoBytes == null)
			errors.add("Upload your photo.");

		if (aadharBytes == null)
			errors.add("Upload your Aadhar PDF.");

		if (panBytes == null)
			errors.add("Upload your PAN PDF.");


		if (!declaration.isChecked())
			errors.add("You must accept the declaration.");

		if (captchaInput.getValue() == null || !captchaInput.getValue().equals(cpa.getValue()))

			errors.add("Captcha does not match.");

		if (!errors.isEmpty()) {

			Messagebox.show(String.join("\n• ", errors), VERROR, Messagebox.OK, Messagebox.EXCLAMATION);

			return;

		}

		String accountNo = dao.generateAccountNumber();

		PersonalDetails pd = new PersonalDetails();

		pd.setAccountType(chk1.isChecked() ? "Savings" : "Current");

		pd.setCreditCard(creditCard.isChecked());

		pd.setFirstName(firstNameBox.getValue());

		pd.setMiddleName(middleNameBox.getValue());

		pd.setLastName(lastNameBox.getValue());

		pd.setDob(dobBox.getValue());

		pd.setNationality(india.isChecked() ? "Indian" : "Others");

		pd.setGuardianName(relativeNameBox.getValue());

		pd.setGender(genderGroup.getSelectedItem() != null ? genderGroup.getSelectedItem().getLabel() : null);

		pd.setCityOfBirth(cityOfBirthBox.getValue());

		pd.setAnnualIncome(new java.math.BigDecimal(incomeBox.getValue()));

		pd.setIdProof(
				idProofBox.getSelectedItem() != null ? idProofBox.getSelectedItem().getLabel() : idProofBox.getValue());

		pd.setOccupation(Occupation.getValue());

		pd.setIdNumber(Idnumber.getValue());

		pd.setCountryCode(
				countryBox.getSelectedItem() != null ? countryBox.getSelectedItem().getValue() : countryBox.getValue());

		pd.setMobileNumber(mobileNumber.getValue());

		pd.setEmail(email.getValue());

		pd.setAccountNo(accountNo);

		AddressDocuments ad = new AddressDocuments();

		ad.setAccountNo(accountNo);

		ad.setCurrentAddress(addressBox.getValue());

		ad.setCurrentCity(cityBox.getValue());

		ad.setCurrentDistrict(districtBox.getValue());

		ad.setCurrentState(stateBox.getValue());

		ad.setCurrentPin(pin.getValue() != null ? pin.getValue().toString() : null);

		ad.setCurrentCountry(country.getValue());

		ad.setPermanentAddress(peraddressBox.getValue());

		ad.setPermanentCity(percity.getValue());

		ad.setPermanentDistrict(perdistrict.getValue());

		ad.setPermanentState(perstate.getValue());

		ad.setPermanentPin(perpin.getValue() != null ? perpin.getValue().toString() : null);

		ad.setPermanentCountry(percountry.getValue());

		ad.setPhotoBytes(getFileBytes());

		ad.setAadharBytes(aadharBytes);

		ad.setPanBytes(panBytes);

		Messagebox.show(dao.insert(pd, ad) ? "Account is created..." : "Account not created...");

	}


	@Listen("onSelect = #tabbox1")

	public void onTabSelected() {

		if (tabbox1.getSelectedTab() == tab6)
			populateReview();

	}

	private void populateReview() {

		String fullName = String.format("%s %s %s",

				nullToEmpty(firstNameBox.getValue()),

				nullToEmpty(middleNameBox.getValue()),

				nullToEmpty(lastNameBox.getValue())).replaceAll("\\s+", " ").trim();

		reviewName.setValue(fullName);

		if (dobBox.getValue() != null)
			reviewDob.setValue(dobBox.getValue().toString());

		Radio selectedGender = genderGroup.getSelectedItem();

		if (selectedGender != null)
			reviewGender.setValue(selectedGender.getLabel());

		reviewMobile.setValue(mobileNumber.getValue() != null ? mobileNumber.getValue().toString() : "");

		reviewEmail.setValue(email.getValue());

		String fullAddress = String.format("%s, %s, %s, %s - %s, %s",

				nullToEmpty(addressBox.getValue()),

				nullToEmpty(cityBox.getValue()),

				nullToEmpty(districtBox.getValue()),

				nullToEmpty(stateBox.getValue()),

				pin.getValue() != null ? pin.getValue().toString() : "",

				nullToEmpty(country.getValue())).replaceAll(",\\s*,", ",");

		reviewAddress.setValue(fullAddress);

		if (chk1.isChecked())
			reviewAccountType.setValue("Savings");

		else if (chk2.isChecked())
			reviewAccountType.setValue("Current");

		else
			reviewAccountType.setValue("Not selected");

	}

	private String nullToEmpty(String s) {
		return s == null ? "" : s;
	}


	@Listen("onClick = #autoFillBtn")

	public void autoFillForm() {

		firstNameBox.setValue("Souvik");

		middleNameBox.setValue("Kumar");

		lastNameBox.setValue("Saha");

		dobBox.setValue(new Date());

		india.setChecked(true);

		other.setChecked(false);

		relativeNameBox.setValue("Rakesh Saha");

		cityOfBirthBox.setValue("Kolkata");

		incomeBox.setValue("600000");

		Occupation.setValue("Software Tester");

		idProofBox.setValue("Aadhar Card");

		Idnumber.setValue("123456789012");

		genderGroup.setSelectedIndex(0);

		chk1.setChecked(true);

		chk2.setChecked(false);

		creditCard.setChecked(true);

		countryBox.setValue("+91");

		mobileNumber.setValue(9876543210L);

		email.setValue("souvik.test@example.com");

		addressBox.setValue("123 Street Name, Area Name");

		cityBox.setValue("Kolkata");

		districtBox.setValue("North 24 Parganas");

		stateBox.setValue("West Bengal");

		pin.setValue(700101);

		country.setValue("India");

		sameAsCurrent.setChecked(true);

		copyCurrentAddress();

		declaration.setChecked(true);

		captchaInput.setValue(cpa.getValue());

		Messagebox.show("Form auto-filled with test data!", "Success", Messagebox.OK, Messagebox.INFORMATION);

	}

}