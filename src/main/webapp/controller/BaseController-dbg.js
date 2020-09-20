/*global history */
sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/core/routing/History"
], function (Controller, History) {
	"use strict";

	return Controller.extend("com.mjzsoft.demo.ui5.GeneralODataViewer.controller.BaseController", {
		/**
		 * Convenience method for accessing the router in every controller of the application.
		 * @public
		 * @returns {sap.ui.core.routing.Router} the router for this component
		 */
		getRouter: function () {
			return this.getOwnerComponent().getRouter();
		},

		/**
		 * Convenience method for getting the view model by name in every controller of the application.
		 * @public
		 * @param {string} sName the model name
		 * @returns {sap.ui.model.Model} the model instance
		 */
		getModel: function (sName) {
			return this.getView().getModel(sName) ? this.getView().getModel(sName) : this.getOwnerComponent().getModel(sName);
		},

		/**
		 * Convenience method for setting the view model in every controller of the application.
		 * @public
		 * @param {sap.ui.model.Model} oModel the model instance
		 * @param {string} sName the model name
		 * @returns {sap.ui.mvc.View} the view instance
		 */
		setModel: function (oModel, sName) {
			return this.getView().setModel(oModel, sName);
		},

		/**
		 * Convenience method for getting the resource bundle.
		 * @public
		 * @returns {sap.ui.model.resource.ResourceModel} the resourceModel of the component
		 */
		getResourceBundle: function () {
			return this.getOwnerComponent().getModel("i18n").getResourceBundle();
		},

		/**
		 * Event handler for navigating back.
		 * It there is a history entry or an previous app-to-app navigation we go one step back in the browser history
		 * If not, it will replace the current entry of the browser history with the master route.
		 * @public
		 */
		onNavBack: function () {
			var sPreviousHash = History.getInstance().getPreviousHash(),
				oCrossAppNavigator = sap.ushell.Container.getService("CrossApplicationNavigation");

			if (sPreviousHash !== undefined || !oCrossAppNavigator.isInitialNavigation()) {
				history.go(-1);
			} else {
				this.getRouter().navTo("master", {}, true);
			}
		},
		
		//extract Entity from metadata
		extractEntity: function (sEntityName, oModel) {
			var aRes = sEntityName.split("."),
				sNamespace = "",
				sEntity,
				oEntity,
				oMetaData = oModel.getServiceMetadata();
				for(var k=0; k<aRes.length - 1; k++){
					sNamespace += aRes[k] + ".";
				}
				if(aRes.length){
					sEntity = aRes[aRes.length - 1];
					sNamespace = sNamespace.slice(0, -1);
				}
			for (var j = 0; j < oMetaData.dataServices.schema.length && aRes.length; j++) {
				if (oMetaData.dataServices.schema[j].namespace === sNamespace) {
					var aEntities = oMetaData.dataServices.schema[j].entityType;
					for (var i = 0; i < aEntities.length; i++) {
						if (aEntities[i].name === sEntity) {
							oEntity = aEntities[i];
							break;
						}
					}
					break;
				}
			}
			return oEntity;
		}, 
		//returns complex types from metadata
		extractComplexTypes: function(sEntityName, oModel) {
				var aRes = sEntityName.split("."),
				sNamespace = aRes[0],
				//sEntity = aRes[1],
				aComplexTypes,
				oMetaData = oModel.getServiceMetadata();
			for (var j = 0; j < oMetaData.dataServices.schema.length; j++) {
				if (oMetaData.dataServices.schema[j].namespace === sNamespace) {
					aComplexTypes = oMetaData.dataServices.schema[j].complexType;
					}
				}
			return aComplexTypes;
		},
		
		
		//returns the keys of the oEntity
		getKeys: function (oEntity) {
			var sKeys = [];

			for (var i = 0; i < oEntity.key.propertyRef.length; i++) {
				sKeys.push(oEntity.key.propertyRef[i].name);
			}
			return sKeys;
		},
		
		//just for EDM. Type
		extractInputType: function(sType) {
			// var oType;
			
			switch (sType) {
				case "Edm.Int32": 
					return sap.m.InputType.Number;
				case "Edm.Int16":
					return sap.m.InputType.Number;
				case "Edm.Decimal":
					return sap.m.InputType.Number;
				case "Edm.String":
					return sap.m.InputType.Text;
				default: 
				return sap.m.InputType.Text; 
			}
		}, 
		//returns JSON object
		concatJSON: function (names, values) {
			var result = {},
				i;
			for (i = 0; i < names.length; i++)
				result[names[i]] = values[i];
			return result;
		},
		
		convertType: function(oObject, sDataType) {
			
			//sap.ui.model.type.Integer
			if (sDataType.includes("Int")) {
					return Number(oObject);
				}
				
			if(sDataType.includes("Date")) {
					oObject = new Date(oObject);
					return oObject === "Invalid Date" ? new Date(oObject.split("/").reserve()) : oObject;
				}
			if(sDataType.include("String")) {
				return String(oObject);
			}
		}
	});

});