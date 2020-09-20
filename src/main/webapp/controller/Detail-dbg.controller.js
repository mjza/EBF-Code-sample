/*global location */
sap.ui.define([
	"com/mjzsoft/demo/ui5/GeneralODataViewer/controller/BaseController",
	"com/mjzsoft/demo/ui5/GeneralODataViewer/model/formatter",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageToast",
	"sap/base/Log",
	"sap/ui/core/Fragment"
], function (BaseController, formatter, JSONModel, MessageToast, Log, Fragment) {
	"use strict";

	return BaseController.extend("com.mjzsoft.demo.ui5.GeneralODataViewer.controller.Detail", {

		formatter: formatter,

		/* =========================================================== */
		/* lifecycle methods                                           */
		/* =========================================================== */

		onInit: function () {
			// Model used to manipulate control states. The chosen values make sure,
			// detail page is busy indication immediately so there is no break in
			// between the busy indication for loading the view's meta data
			var oViewModel = new JSONModel({
				tableGenerating: true,
				busy: false,
				delay: 0,
				lineItemListTitle: this.getResourceBundle().getText("detailLineItemTableHeading"),
				__metadata: {
					uri: "URI",
					type: "Type"
				}
			});

			this.setModel(oViewModel, "detailView");

			var oData = {
				DataSet: []
			};
			this._aSelectedItems = [];
			this._oEntity = null;
			//Creation of the JSON Model for the Test Daya
			// var oDataModel = new sap.ui.model.json.JSONModel(oData);
			// this.setModel(oDataModel, "DataModel");

			var oNewModel = new JSONModel();
			this.setModel(oNewModel, "newDataModel");

			this.getModel("DataDetailModel").setData(oData);

			this.getRouter().getRoute("object").attachPatternMatched(this._onObjectMatched, this);

			this.getOwnerComponent().getModel().metadataLoaded().then(this._onMetadataLoaded.bind(this));
		},

		/* =========================================================== */
		/* event handlers                                              */
		/* =========================================================== */

		/**
		 * Event handler when the share by E-Mail button has been clicked
		 * @public
		 */
		onSendEmailPress: function () {
			var oViewModel = this.getModel("detailView");

			sap.m.URLHelper.triggerEmail(
				null,
				oViewModel.getProperty("/shareSendEmailSubject"),
				oViewModel.getProperty("/shareSendEmailMessage")
			);
		},

		/**
		 * Event handler when the share in JAM button has been clicked
		 * @public
		 */
		onShareInJamPress: function () {
			var oViewModel = this.getModel("detailView"),
				oShareDialog = sap.ui.getCore().createComponent({
					name: "sap.collaboration.components.fiori.sharing.dialog",
					settings: {
						object: {
							id: location.href,
							share: oViewModel.getProperty("/shareOnJamTitle")
						}
					}
				});

			oShareDialog.open();
		},

		/**
		 * Updates the item count within the line item table's header
		 * @param {object} oEvent an event containing the total number of items in the list
		 * @private
		 */
		onListUpdateFinished: function (oEvent) {
			var sTitle,
				iTotalItems = oEvent.getParameter("total"),
				oViewModel = this.getModel("detailView");

			// only update the counter if the length is final
			if (this.byId("lineItemsList").getBinding("items").isLengthFinal()) {
				if (iTotalItems) {
					sTitle = this.getResourceBundle().getText("detailLineItemTableHeadingCount", [iTotalItems]);
				} else {
					//Display 'Line Items' instead of 'Line items (0)'
					sTitle = this.getResourceBundle().getText("detailLineItemTableHeading");
				}
				oViewModel.setProperty("/lineItemListTitle", sTitle);
			}
		},

		/* =========================================================== */
		/* begin: internal methods                                     */
		/* =========================================================== */

		/**
		 * Binds the view to the object path and expands the aggregated line items.
		 * @function
		 * @param {sap.ui.base.Event} oEvent pattern match event in route 'object'
		 * @private
		 */
		_onObjectMatched: function (oEvent) {
			var sSetName = oEvent.getParameter("arguments").SetName,
				sEntityName = oEvent.getParameter("arguments").EntityName,
				oViewModel = this.getModel("appView");
			this._sSetName = sSetName;
			this._oEntityName = sEntityName;
			oViewModel.setProperty("/layout", "TwoColumnsMidExpanded");
			oViewModel.setProperty("/busy", true);
			var oModel = this.getModel();
			oModel.metadataLoaded().then(function () {
				oModel = this.getModel();
				oModel.setUseBatch(false);
				oModel.read("/" + sSetName, {
					success: function (oData, oResponse) {
						oViewModel.setProperty("/busy", false);
						var oItems = {
							DataSet: oData.results
						};
						this.getModel("DataDetailModel").setData(oItems);
						oViewModel.setProperty("/tableGenerating", true);
						this.createSmartTable(sEntityName, sSetName);
					}.bind(this)
				});
			}.bind(this));
		},
		//erstellen einer Smart-Table, abhängig der Daten mit Spalten und Zeilen
		createSmartTable: function (sEntityName, sSetName) {
			var oEntity = this.extractEntity(sEntityName, this.getModel()),
				oViewModel = this.getModel("detailView");
			if (oEntity) {
				this._oEntity = oEntity;
				var aCells = [];
				for (var i = 0; i < oEntity.property.length; i++) {
					var oCol = oEntity.property[i];
					var sText = oCol.name;
					if (oCol.extensions) {
						for (var j = 0; j < oCol.extensions.length; j++) {
							if (oCol.extensions[j].name === "label") {
								sText = oCol.extensions[j].value;
								break;
							}
						}
					}
					aCells.push({
						col: oCol.name,
						txt: sText
					});
				}
				if (aCells.length > 0) {
					var sCols = "",
						sCels = "";
					for (i = 0; i < aCells.length; i++) {
						sCols += "" +
							"	        <m:Column visible='true'> \n" +
							"		       <m:customData> \n" +
							"			      <core:CustomData key='p13nData' \n" +
							"				     value='\\{\"sortProperty\":\"" + aCells[i].col + "\",\"filterProperty\":\"" + aCells[i].col + "\",\"columnKey\":\"" +
							aCells[i].col + "\",\"leadingProperty\":\"" + aCells[i].col + "\",\"columnIndex\":\"" + i + "\"}'/> \n" +
							"		       </m:customData> \n" +
							"		       <m:Text text='" + aCells[i].txt + "'/> \n" +
							"	        </m:Column> \n";
						sCels += "" +
							"			   <m:Text text=\"{path:'DataDetailModel>" + aCells[i].col + "'}\"/> \n";
					}
					// var oViewModel = this.getModel("detailView");
					oViewModel.setProperty("/setItems", sSetName);

					var oModelX = new sap.ui.model.xml.XMLModel();
					oModelX.attachRequestCompleted(function () {
						var xmlStr = oModelX.getXML();
						xmlStr = xmlStr.replace("<!--cols-->", sCols).replace("<!--cels-->", sCels);
						//
						var oLayout = this.getView().byId("myLayout");
						var aControls = oLayout.removeAllContent();
						for (var j = 0; j < aControls.length; j++) {
							var oControl = aControls[j];
							if (typeof oControl.destroy === "function") {
								oControl.destroy();
							}
						}
						Fragment.load({
							type: "XML",
							id: sSetName,
							definition: xmlStr,
							controller: this
						}).then(function (oControll) {
							oLayout.addContent(oControll);
							oViewModel.setProperty("/tableGenerating", false);
						});
					}.bind(this));
					oModelX.loadData(jQuery.sap.getModulePath("com/mjzsoft/demo/ui5/GeneralODataViewer/view/") + "/SmartTable.fragment.xml");
				}
			} else {
				Log.error("Couldn't find oEntry.");
			}
		},
		// createTable: function (sEntityName) {
		// 	var oTable = this.byId("tableDataSet"),
		// 		oEntity = this.extractEntity(sEntityName, this.getModel());
		// 	if (oEntity) {
		// 		oTable.removeAllColumns();
		// 		oTable.unbindItems();
		// 		var aCells = [];
		// 		for (var i = 0; i < oEntity.property.length; i++) {
		// 			var oCol = oEntity.property[i];
		// 			var oColumn = new sap.m.Column({
		// 				header: new sap.m.Label({
		// 					text: oCol.name
		// 				})
		// 			});
		// 			oColumn.addCustomData(
		// 				new sap.ui.core.CustomData({
		// 					key: "p13nData",
		// 					value: "{'sortProperty': '" + oCol.name + "', 'filterProperty': '" + oCol.name + "', 'columnKey': '" + oCol.name +
		// 						"', 'leadingProperty': '" + oCol.name + "', 'columnIndex':'" + i + "'}"
		// 				}));
		// 			aCells.push(new sap.m.Text({
		// 				text: "{DataDetailModel>" + oCol.name + "}"
		// 			}));
		// 			oTable.addColumn(oColumn);
		// 		}

		// 		oTable.bindItems({
		// 			path: "DataDetailModel>/DataSet",
		// 			template: new sap.m.ColumnListItem({
		// 				cells: aCells
		// 			})
		// 		});
		// 	}
		// },

		/**
		 * Binds the view to the object path. Makes sure that detail view displays
		 * a busy indicator while data for the corresponding element binding is loaded.
		 * @function
		 * @param {string} sObjectPath path to the object to be bound to the view.
		 * @private
		 */
		_bindView: function (sObjectPath) {
			// Set busy indicator during view binding
			var oViewModel = this.getModel("detailView");

			// If the view was not bound yet its not busy, only if the binding requests data it is set to busy again
			oViewModel.setProperty("/busy", false);

			this.getView().bindElement({
				path: sObjectPath,
				events: {
					change: this._onBindingChange.bind(this),
					dataRequested: function () {
						oViewModel.setProperty("/busy", true);
					},
					dataReceived: function () {
						oViewModel.setProperty("/busy", false);
					}
				}
			});
		},

		_onBindingChange: function () {
			var oView = this.getView(),
				oElementBinding = oView.getElementBinding();

			// No data for the binding
			if (!oElementBinding.getBoundContext()) {
				this.getRouter().getTargets().display("detailObjectNotFound");
				// if object could not be found, the selection in the master list
				// does not make sense anymore.
				this.getOwnerComponent().oListSelector.clearMasterListSelection();
				return;
			}

			var sPath = oElementBinding.getPath(),
				oResourceBundle = this.getResourceBundle(),
				oObject = oView.getModel().getObject(sPath),
				sObjectId = oObject.CategoryID,
				sObjectName = oObject.CategoryName,
				oViewModel = this.getModel("detailView");

			this.getOwnerComponent().oListSelector.selectAListItem(sPath);

			oViewModel.setProperty("/saveAsTileTitle", oResourceBundle.getText("shareSaveTileAppTitle", [sObjectName]));
			oViewModel.setProperty("/shareOnJamTitle", sObjectName);
			oViewModel.setProperty("/shareSendEmailSubject",
				oResourceBundle.getText("shareSendEmailObjectSubject", [sObjectId]));
			oViewModel.setProperty("/shareSendEmailMessage",
				oResourceBundle.getText("shareSendEmailObjectMessage", [sObjectName, sObjectId, location.href]));
		},

		_onMetadataLoaded: function () {
			// Store original busy indicator delay for the detail view
			var iOriginalViewBusyDelay = this.getView().getBusyIndicatorDelay(),
				oViewModel = this.getModel("detailView");
			//	oLineItemTable = this.byId("lineItemsList"),
			//iOriginalLineItemTableBusyDelay = oLineItemTable.getBusyIndicatorDelay();

			// Make sure busy indicator is displayed immediately when
			// detail view is displayed for the first time
			oViewModel.setProperty("/delay", 0);
			oViewModel.setProperty("/lineItemTableDelay", 0);

			// oLineItemTable.attachEventOnce("updateFinished", function () {
			// 	// Restore original busy indicator delay for line item table
			// 	oViewModel.setProperty("/lineItemTableDelay", iOriginalLineItemTableBusyDelay);
			// });

			// Binding the view will set it to not busy - so the view is always busy if it is not bound
			oViewModel.setProperty("/busy", false);
			// Restore original busy indicator delay for the detail view
			//oViewModel.setProperty("/delay", iOriginalViewBusyDelay);
		},

		/**
		 * Set the full screen mode to false and navigate to master page
		 */
		onCloseDetailPress: function () {
			this.getModel("appView").setProperty("/actionButtonsInfo/midColumn/fullScreen", false);
			// No item should be selected on master after detail page is closed
			this.getOwnerComponent().oListSelector.clearMasterListSelection();
			this.getRouter().navTo("master");
		},

		/**
		 * Toggle between full and non full screen mode.
		 */
		toggleFullScreen: function () {
			var bFullScreen = this.getModel("appView").getProperty("/actionButtonsInfo/midColumn/fullScreen");
			this.getModel("appView").setProperty("/actionButtonsInfo/midColumn/fullScreen", !bFullScreen);
			if (!bFullScreen) {
				// store current layout and go full screen
				this.getModel("appView").setProperty("/previousLayout", this.getModel("appView").getProperty("/layout"));
				this.getModel("appView").setProperty("/layout", "MidColumnFullScreen");
			} else {
				// reset to previous layout
				this.getModel("appView").setProperty("/layout", this.getModel("appView").getProperty("/previousLayout"));
			}
		},

		//unused 
		//auch wieder hier einen Dialog einbauen
		onPressDeleteDetail: function (oEvent) {
			var oDataModel = this.getModel("DataDetailModel"),
				aKeys = this.getKeys(this._oEntity),
				oItem,
				iIndex,
				sObjectPath,
				aVals = [],
				oKeyValuePair = {};

			//get Key properties of the entity
			//aber keine Werte mit befüllen

			for (var i = 0; i < this._aSelectedItems.length; i++) {
				iIndex = this._aSelectedItems[i];
				oItem = oDataModel.getData().DataSet[iIndex];
				// oDataModel.getData().DataSet.splice(iIndex, 1);
				// oDataModel.refresh(true);
				for (var b = 0; b < aKeys.length; b++) {
					aVals.push(oItem[aKeys[b]]);
				}

				oKeyValuePair = this.concatJSON(aKeys, aVals);
				aVals = [];

				sObjectPath = this.getModel().createKey(this._sSetName,
					oKeyValuePair
				);

				this.getModel().remove("/" + sObjectPath, {
					success: function () {
						oDataModel.getData().DataSet.splice(iIndex, 1);
						oDataModel.refresh(true);
						MessageToast.show("Items deleted");

					}.bind(this),
					error: function (oError) {
						MessageToast.show("Delete Error");

					}.bind(this)
				});

				this.getModel().submitChanges({});

				//oDataModel.getData().DataSet.splice(iIndex, 1);
				//oDataModel.refresh(true);

			}
			//Uncheck all Items 

			this.aSelectedItems = [];

		},

		onSelectionChangedDetail: function (oEvent) {
			//var bSelected =	oEvent.getSource().getSelectedItem().getSelected(),
			var aSelectedContextPaths = oEvent.getSource().getSelectedContextPaths(),
				aIndex = [];
			for (var i = 0; i < aSelectedContextPaths.length; i++) {
				//vielleicht reicht es auch hier String.split anzuwenden
				aIndex.push(aSelectedContextPaths[i].match(/\d+/g).map(Number)[0]);
			}
			this._aSelectedItems = aIndex;
		},

		//Navigate to Object View
		onTableItemPress: function (oEvent) {
			var sPath = oEvent.getSource().getBindingContextPath();

			this.getModel("appView").setProperty("/layout", "ThreeColumnsEndExpanded");

			this.getRouter().navTo("detailObject", {
				Set: this._sSetName,
				EntityName: this._oEntityName,
				Index: sPath.match(/\d+/g).map(Number)[0]
			});
		},

		//ADD
		onPressAdd: function (oEvent) {
			var oFormContainer,
				oControl,
				oFormElement,
				sProperty,
				sText,
				sType;

			if (!this._oAddDialog) {
				this._oAddDialog = sap.ui.xmlfragment("detail", "com.mjzsoft.demo.ui5.GeneralODataViewer.view.Add", this);
				this.getView().addDependent(this._oAddDialog);
			}

			this._oAddDialog.open();
			oFormContainer = this._oAddDialog.getContent()[0].getFormContainers()[0];
			oFormContainer.removeAllFormElements();

			for (var i = 0; i < this._oEntity.property.length; i++) {
				sProperty = this._oEntity.property[i].name;
				sText = this._oEntity.property[i].name;
				sType = this._oEntity.property[i].type;

				if (this._oEntity.property[i].extensions) {
					for (var j = 0; j < this._oEntity.property[i].extensions.length; j++) {
						if (this._oEntity.property[i].extensions[j].name === "label") {
							sText = this._oEntity.property[i].extensions[j].value;
							break;
						}
					}
				}

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
					label: sText,
					fields: oControl
				});
				oFormElement.data("property", sProperty);
				oFormContainer.addFormElement(oFormElement);
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
			oModel.create("/" + this._sSetName, oProperties, {
				success: function (oData, oResponse) {
					oDataModel.getData().DataSet.push(oData);
					oDataModel.refresh(true);
					MessageToast.show("Successfully added a new Object");
				}
			});
			oModel.submitChanges({
				success: function () {

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
		},

		onInputLiveChange: function (oEvent) {
			var sType = oEvent.getSource().getType(),
				oValue = oEvent.getParameters().newValue;
			if (sType === "Number") {
				oValue = Number(oValue);
			}
			this.getModel("newDataModel").setProperty("/" + oEvent.getSource().getParent().data("property"), oValue);
		},

		onSelectionChange: function (oEvent) {
			var oSource = oEvent.getSource();
			this.getModel("newDataModel").setProperty("/" + oSource.getParent().data("property"), oSource.getSelected());
		},

		onChangeDatePicker: function (oEvent) {
			//var	dNewValue = new Date(oEvent.getParameter("newValue"));
			var bValid = oEvent.getParameter("valid"),
				sProperty;
			if (bValid) {
				sProperty = oEvent.getSource().getParent().data("property");
				//ditingush between new model and current model
				this.getModel("newDataModel").setProperty("/" + sProperty, oEvent.getSource().getDateValue());
			}
		}

	});

});