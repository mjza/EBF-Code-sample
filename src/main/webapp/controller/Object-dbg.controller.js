sap.ui.define([
	"sap/m/MessageToast",
	"sap/ui/model/json/JSONModel",
	"sap/ui/Device",
	"com/mjzsoft/demo/ui5/GeneralODataViewer/controller/BaseController"
], function (MessageToast, JSONModel, Device, BaseController) {
	"use strict";

	return BaseController.extend("com.mjzsoft.demo.ui5.GeneralODataViewer.controller.Object", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.mjzsoft.demo.ui5.GeneralODataViewer.view.Object
		 */
		onInit: function () {
			var oViewObjectModel = new JSONModel({
					setName: "",
					pageBusy: false,
					editable: false,
					hasChanges: false,
					title: ""
				}),
				oDataModel,
				oNewModel;

			this._aComplexTypes = [];
			this.setModel(oViewObjectModel, "objectView");

			oDataModel = new JSONModel();
			this.setModel(oDataModel, "DataModel");

			oNewModel = new JSONModel();
			this.setModel(oNewModel, "newDataModel");

			this.getRouter().getRoute("detailObject").attachPatternMatched(this._onObjectMatched, this);
		},

		//überarbeiten -> Problem, bei mehreren Keys
		_onObjectMatched: function (oEvent) {
			var sSetName = oEvent.getParameter("arguments").Set,
				oViewModel = this.getModel("objectView"),
				sEntityName = oEvent.getParameter("arguments").EntityName,
				index = Number(oEvent.getParameter("arguments").Index),
				oEntity = this.extractEntity(sEntityName, this.getModel()),
				aComplexTypes = this.extractComplexTypes(sEntityName, this.getModel()),
				aKeys = this.getKeys(oEntity),
				oModel = this.getModel(),
				oItems,
				oKeyValue,
				aValues = [];
			this._index = index;
			this._setName = sSetName;
			this._oDataModel = this.getModel("DataModel");

			if (index === undefined) {
				this.onNavBack();
			}
			//oViewModel.setProperty("/title", oEntity.name);
			oViewModel.setProperty("/busy", true);
			oViewModel.setProperty("/editable", false);
			oViewModel.setProperty("/hasChanges", false);
			this.byId("idSmartFormGroup").removeAllGroupElements();

			if (this._oAddDialog) {
				this._oAddDialog.getContent()[0].getFormContainers()[0].removeAllFormElements();
			}

			oModel.metadataLoaded().then(function () {
				oModel.read("/" + sSetName, {
					success: function (oData, oResponse) {
						oItems = {
							DataSet: oData.results
						};
						this._oDataModel.setData(oItems.DataSet[index]);

						for (var i = 0; i < aKeys.length; i++) {
							aValues.push(this._oDataModel.getProperty("/" + aKeys[i]));
						}
						oKeyValue = this.concatJSON(aKeys, aValues);

						this._sObjectPath = this.getModel().createKey(sSetName,
							oKeyValue
						);

						this.getView().bindElement({
							path: "/" + this._sObjectPath
						});

						this.createSmartForm(oEntity, aComplexTypes);
					}.bind(this),

					error: function () {
						oViewModel.setProperty("/busy", false);
					}.bind(this)
				});
			}.bind(this));
		},

		createSmartForm: function (oEntity, aComplexTypes) {

			if (oEntity) {
				var oForm = this.byId("idSmartForm"),
					oGroup = this.byId("idSmartFormGroup"),
					oSmartField,
					aOdataTypes = [],
					oGroupElement;
				this._oEntity = oEntity;
				oForm.setTitle(oEntity.name);

				if (aComplexTypes) {
					for (var b = 0; b < aComplexTypes.length; b++) {
						var sName = oEntity.namespace + "." + aComplexTypes[b].name;
						aOdataTypes.push(sName);
					}
				}

				for (var i = 0; i < oEntity.property.length; i++) {

					oGroupElement = new sap.ui.comp.smartform.GroupElement();
					var oType = oEntity.property[i].type;

					if (oType.includes("Edm.")) {
						var sText = oEntity.property[i].name;
						if (oEntity.property[i].extensions) {
							for (var j = 0; j < oEntity.property[i].extensions.length; j++) {
								if (oEntity.property[i].extensions[j].name === "label") {
									sText = oEntity.property[i].extensions[j].value;
									break;
								}
							}
						}
						oSmartField = new sap.ui.comp.smartfield.SmartField({
							textLabel: sText,
							value: "{" + oEntity.property[i].name + "}"
						});
						oSmartField.attachChange(this.onEditableChanged);
						oGroupElement.addElement(oSmartField);
						oGroup.addGroupElement(oGroupElement);
					} else {

						if (aOdataTypes.includes(oType)) {

							for (var a = 0; a < aComplexTypes.length; a++) {

								if (oType.split(".")[1] === aComplexTypes[a].name) {

									var aHelperArray = aComplexTypes[a].property;

									for (var z = 0; z < aHelperArray.length; z++) {
										if (aHelperArray[z].type.includes("Edm.")) {

											oSmartField = new sap.ui.comp.smartfield.SmartField({
												textLabel: aHelperArray[z].name,
												value: "Test"
													//value: "{"+ oEntity.property[i].name + "/"+ aHelperArray[z].name + "}"
											});
											//oSmartField.setValue("Bahnste");
											oSmartField.attachChange(this.onEditableChanged);
											oGroupElement.addElement(oSmartField);
											oGroup.addGroupElement(oGroupElement);
										}
									}
								}
							}
						}

					}
				}
				this.getModel("objectView").setProperty("/busy", false);
			}
		},

		onEditableChanged: function (oEvent) {
			var sProperty = oEvent.getSource().getTextLabel(),
				oNewValue = oEvent.getParameters().newValue,
				sDataType = this.getDataType();

			if (sDataType.includes("Edm.")) {
				//umbauen-> nicht generisch genug
				if (sDataType.includes("Int") || sDataType.includes("Single")) {
					oNewValue = Number(oNewValue);
				}

				if (sDataType.includes("Decimal")) {
					oNewValue = parseFloat(oNewValue);
				}

				if (sDataType.includes("Date")) {
					oNewValue = oEvent.getSource().getContent().getDateValue();
				}

				this.getModel("DataModel").setProperty("/" + sProperty, oNewValue);
				this.getModel("objectView").setProperty("/hasChanges", true);
			}
		},

		onPressDelete: function (oEvent) {
			var oModel = this.getView().getModel("DataDetailModel"),
				oData,
				oConfirmDialog;

			oConfirmDialog = new sap.m.Dialog({
				title: "Delete",
				type: sap.m.DialogType.Message,
				state: sap.ui.core.ValueState.Warning,
				content: new sap.m.Text({
					text: "Are you sure you want to delete?"
				}),
				beginButton: new sap.m.Button({
					text: "No",
					type: "Reject",
					press: function () {
						MessageToast.show("Canceled");
						oConfirmDialog.close();
					}
				}),
				endButton: new sap.m.Button({
					text: "Yes",
					type: "Accept",
					press: function () {
						this.getView().getModel().remove("/" + this._sObjectPath, {
							success: function () {
								oData = oModel.getData().DataSet;
								oData.splice(this._index, 1);
								oModel.refresh(true);
								MessageToast.show("Item deleted");
								this.navFromObjectToDetail(this._setName, this._oEntity.entityType);
							}.bind(this),
							error: function () {
								MessageToast.show("Delete Error");
								this.navFromObjectToDetail(this._setName, this._oEntity.entityType);
							}.bind(this)
						});

						oConfirmDialog.close();
						this.onNavBack();
					}.bind(this)
				})
			});
			oConfirmDialog.open();
		},

		//ADD
		onPressAdd: function (oEvent) {
			var oFormContainer,
				oControl,
				oFormElement,
				sProperty,
				sType;

			if (!this._oAddDialog) {
				this._oAddDialog = sap.ui.xmlfragment("com.mjzsoft.demo.ui5.GeneralODataViewer.view.Add", this);
				this.getView().addDependent(this._oAddDialog);
			}

			this._oAddDialog.open();
			oFormContainer = this._oAddDialog.getContent()[0].getFormContainers()[0];
			oFormContainer.removeAllFormElements();

			for (var i = 0; i < this._oEntity.property.length; i++) {
				sProperty = this._oEntity.property[i].name;
				sType = this._oEntity.property[i].type;

				switch (sType) {
				case "Edm.Boolean":
					oControl = new sap.m.CheckBox();
					oControl.attachSelect(this.onSelectionChange);
					break;
				case "Edm.DateTime":
					oControl = new sap.m.DatePicker({
						value: "{path:'newDataModel>/" + sProperty + "'," + "type:'sap.ui.model.type.Date'}"
					});

					oControl.attachChange(this.onChangeDatePicker);
					if (this.getKeys(this._oEntity).includes(sProperty)) {
						oControl.setRequired(true);
					}
					break;
				default:
					oControl = new sap.m.Input({
						value: "{newDataModel>/" + sProperty + "}",
						//value: "{path:'newDataModel>/RegionID', type:'sap.ui.model.type.Int'}",
						type: this.extractInputType(this._oEntity.property[i].type)
					});
					oControl.attachLiveChange(this.onInputLiveChange);
					//for key properties
					//maybe check nullable property
					if (this.getKeys(this._oEntity).includes(sProperty)) {
						oControl.setRequired(true);
					}
					break;
				}

				oFormElement = new sap.ui.layout.form.FormElement({
					label: sProperty,
					fields: oControl
				});
				oFormContainer.addFormElement(oFormElement);
			}
		},

		onInputLiveChange: function (oEvent) {
			var sType = oEvent.getSource().getType(),
				oValue = oEvent.getParameters().newValue,
				sLabelName = oEvent.getSource().getParent().getLabel();

			if (sType === "Number") {
				oValue = Number(oValue);
			}
			this.getModel("newDataModel").setProperty("/" + sLabelName, oValue);
		},

		onSelectionChange: function (oEvent) {
			var oSource = oEvent.getSource();
			this.getModel("newDataModel").setProperty("/" + oSource.getParent().getLabel(), oSource.getSelected());
		},

		onChangeDatePicker: function (oEvent) {
			//var	dNewValue = new Date(oEvent.getParameter("newValue"));
			var bValid = oEvent.getParameter("valid"),
				oLabel;
			if (bValid) {
				oLabel = oEvent.getSource().getParent().getLabel();
				//ditingush between new model and current model
				this.getModel("newDataModel").setProperty("/" + oLabel, oEvent.getSource().getDateValue());
			}
		},

		//EDIT
		//Metadaten -> sap:updatable-
		//Problem mit der Formatierung
		onEditToggled: function (oEvent) {
			var oDataDetailModel = this.getModel("DataDetailModel"),
				oViewModel = this.getModel("objectView"),
				oModel = this.getView().getModel(),
				oProperties,
				sPath;

			if (!oEvent.getParameters("editable").editable) {
				if (oViewModel.getProperty("/hasChanges")) {
					oViewModel.setProperty("/busy", true);
					oProperties = this._oDataModel.getData();
					sPath = this.getView().getBindingContext().sPath;

					oModel.update(sPath, oProperties, {
						success: function () {
							oDataDetailModel.getData().DataSet[this._index] = oProperties;
							oDataDetailModel.refresh(true);
							MessageToast.show("Updated successfuly current object");
							oViewModel.setProperty("/hasChanges", false);
							oViewModel.setProperty("/busy", false);
						}.bind(this),
						error: function () {
							oViewModel.setProperty("/busy", false);
							this.navFromObjectToDetail(this._setName, this._oEntity.entityType);
							MessageToast.show("Error");
						}.bind(this)
					});
				}
			}
		},

		//Dialog-Handler
		//
		onPressCancel: function (oEvent) {
			this.getModel("newDataModel").setData({});
			if (this._oAddDialog) {
				if (this._oAddDialog.isOpen()) {
					this._oAddDialog.getContent()[0].getFormContainers()[0].removeAllFormElements();
					this._oAddDialog.close();
				}
			}
		},

		onPressSave: function (oEvent) {
			var oProperties = this.getModel("newDataModel").getData(),
				oDataModel = this.getModel("DataDetailModel"),
				oModel = this.getModel();
			oModel.create("/" + this._setName, oProperties);
			oModel.submitChanges({
				success: function () {
					oDataModel.getData().DataSet.push(oProperties);
					oDataModel.refresh(true);
					MessageToast.show("Successfully added a new Object");
				},
				error: function () {
					MessageToast.show("Error");
				}
			});
			if (this._oAddDialog) {
				if (this._oAddDialog.isOpen()) {
					this._oAddDialog.getContent()[0].getFormContainers()[0].removeAllFormElements();
					this._oAddDialog.close();
				}
			}
			this.getModel("newDataModel").setData({});
			this.onNavBack();
		},

		//NothwindModel.Customer 
		//Customers
		navFromObjectToDetail: function (sSetName, sEntityName) {
			var bReplace = !Device.system.phone;
			// set the layout property of FCL control to show two columns
			this.getModel("appView").setProperty("/layout", "TwoColumnsMidExpanded");
			this.getRouter().navTo("object", {
				SetName: sSetName,
				EntityName: sEntityName
			}, bReplace);
		}
	});

});