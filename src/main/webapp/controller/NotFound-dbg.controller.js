sap.ui.define([
	"com/mjzsoft/demo/ui5/GeneralODataViewer/controller/BaseController"
	], function (BaseController) {
		"use strict";

		return BaseController.extend("com.mjzsoft.demo.ui5.GeneralODataViewer.controller.NotFound", {

			onInit: function () {
				this.getRouter().getTarget("notFound").attachDisplay(this._onNotFoundDisplayed, this);
			},

			_onNotFoundDisplayed : function () {
					this.getModel("appView").setProperty("/layout", "OneColumn");
			}
		});
	}
);