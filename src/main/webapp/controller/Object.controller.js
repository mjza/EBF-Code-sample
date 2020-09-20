sap.ui.define(["sap/m/MessageToast","sap/ui/model/json/JSONModel","sap/ui/Device","sap/ui/core/format/NumberFormat","com/mjzsoft/demo/ui5/GeneralODataViewer/controller/BaseController"],function(e,t,a,o,i){"use strict";return i.extend("com.mjzsoft.demo.ui5.GeneralODataViewer.controller.Object",{onInit:function(){var e=new t({setName:"",pageBusy:false,editable:false,hasChanges:false,title:""}),a,o;this._aComplexTypes=[];this.setModel(e,"objectView");a=new t;this.setModel(a,"DataModel");o=new t;this.setModel(o,"newDataModel");this.getRouter().getRoute("detailObject").attachPatternMatched(this._onObjectMatched,this)},_onObjectMatched:function(e){var t=e.getParameter("arguments").Set,a=this.getModel("objectView"),o=e.getParameter("arguments").EntityName,i=Number(e.getParameter("arguments").Index),s=this.extractEntity(o,this.getModel()),n=this.extractComplexTypes(o,this.getModel()),r=this.getKeys(s),l=this.getModel(),d,h,p=[];this._index=i;this._setName=t;this._oDataModel=this.getModel("DataModel");if(i===undefined){this.onNavBack()}a.setProperty("/busy",true);a.setProperty("/editable",false);a.setProperty("/hasChanges",false);this.byId("idSmartFormGroup").removeAllGroupElements();if(this._oAddDialog){this._oAddDialog.getContent()[0].getFormContainers()[0].removeAllFormElements()}l.metadataLoaded().then(function(){l.read("/"+t,{success:function(e,a){d={DataSet:e.results};this._oDataModel.setData(d.DataSet[i]);for(var o=0;o<r.length;o++){p.push(this._oDataModel.getProperty("/"+r[o]))}h=this.concatJSON(r,p);this._sObjectPath=this.getModel().createKey(t,h);this.getView().bindElement({path:"/"+this._sObjectPath});this.createSmartForm(s,n)}.bind(this),error:function(){a.setProperty("/busy",false)}.bind(this)})}.bind(this))},createSmartForm:function(e,t){if(e){var a=this.byId("idSmartForm"),o=this.byId("idSmartFormGroup"),i,s=[],n;this._oEntity=e;a.setTitle(e.name);if(t){for(var r=0;r<t.length;r++){var l=e.namespace+"."+t[r].name;s.push(l)}}for(var d=0;d<e.property.length;d++){n=new sap.ui.comp.smartform.GroupElement;var h=e.property[d].type;if(h.includes("Edm.")){var p=e.property[d].name;if(e.property[d].extensions){for(var u=0;u<e.property[d].extensions.length;u++){if(e.property[d].extensions[u].name==="label"){p=e.property[d].extensions[u].value;break}}}i=new sap.ui.comp.smartfield.SmartField({textLabel:p,value:"{"+e.property[d].name+"}"});i.data("property",e.property[d].name);i.attachChange(this.onEditableChanged);n.addElement(i);o.addGroupElement(n)}else{if(s.includes(h)){for(var c=0;c<t.length;c++){if(h.split(".")[1]===t[c].name){var g=t[c].property;for(var m=0;m<g.length;m++){if(g[m].type.includes("Edm.")){i=new sap.ui.comp.smartfield.SmartField({textLabel:g[m].name,value:"Test"});i.data("property",g[m].name);i.attachChange(this.onEditableChanged);n.addElement(i);o.addGroupElement(n)}}}}}}}this.getModel("objectView").setProperty("/busy",false)}},onEditableChanged:function(e){var t=e.getSource().data("property"),a=e.getParameters().newValue,i=this.getDataType();if(i.includes("Edm.")){if(i.includes("Int")||i.includes("Single")){a=Number(a)}if(i.includes("Decimal")||i.includes("Double")){var s=o.getFloatInstance();a=s.parse(a)}if(i.includes("Date")){a=e.getSource().getContent().getDateValue()}this.getModel("DataModel").setProperty("/"+t,a);this.getModel("objectView").setProperty("/hasChanges",true)}},onPressDelete:function(t){var a=this.getView().getModel("DataDetailModel"),o,i;i=new sap.m.Dialog({title:"Delete",type:sap.m.DialogType.Message,state:sap.ui.core.ValueState.Warning,content:new sap.m.Text({text:"Are you sure you want to delete?"}),beginButton:new sap.m.Button({text:"No",type:"Reject",press:function(){e.show("Canceled");i.close()}}),endButton:new sap.m.Button({text:"Yes",type:"Accept",press:function(){this.getView().getModel().remove("/"+this._sObjectPath,{success:function(){o=a.getData().DataSet;o.splice(this._index,1);a.refresh(true);e.show("Item deleted");this.navFromObjectToDetail(this._setName,this._oEntity.entityType)}.bind(this),error:function(){e.show("Delete Error");this.navFromObjectToDetail(this._setName,this._oEntity.entityType)}.bind(this)});i.close();this.onNavBack()}.bind(this)})});i.open()},onPressAdd:function(e){var t,a,o,i,s,n,r;if(!this._oAddDialog){this._oAddDialog=sap.ui.xmlfragment("object","com.mjzsoft.demo.ui5.GeneralODataViewer.view.Add",this);this.getView().addDependent(this._oAddDialog)}this._oAddDialog.open();t=this._oAddDialog.getContent()[0].getFormContainers()[0];t.removeAllFormElements();for(var l=0;l<this._oEntity.property.length;l++){i=this._oEntity.property[l].name;s=this._oEntity.property[l].name;n=this._oEntity.property[l].type;r=false;if(this._oEntity.property[l].extensions){for(var d=0;d<this._oEntity.property[l].extensions.length;d++){if(this._oEntity.property[l].extensions[d].name==="label"){s=this._oEntity.property[l].extensions[d].value;break}}for(d=0;d<this._oEntity.property[l].extensions.length;d++){if(this._oEntity.property[l].extensions[d].name==="creatable"){r=this._oEntity.property[l].extensions[d].value==="false";break}}}if(r){continue}switch(n){case"Edm.Boolean":a=new sap.m.CheckBox;a.attachSelect(this.onSelectionChange);break;case"Edm.DateTime":a=new sap.m.DatePicker({value:"{path:'newDataModel>/"+i+"',"+"type:'sap.ui.model.type.Date'}"});a.attachChange(this.onChangeDatePicker);if(this.getKeys(this._oEntity).includes(i)){a.setRequired(true)}break;default:a=new sap.m.Input({value:"{newDataModel>/"+i+"}",type:this.extractInputType(this._oEntity.property[l].type)});a.attachLiveChange(this.onInputLiveChange);if(this.getKeys(this._oEntity).includes(i)){a.setRequired(true)}break}o=new sap.ui.layout.form.FormElement({label:s,fields:a});o.data("property",i);t.addFormElement(o)}},onInputLiveChange:function(e){var t=e.getSource().getType(),a=e.getParameters().newValue;if(t==="Number"){a=Number(a)}this.getModel("newDataModel").setProperty("/"+e.getSource().getParent().data("property"),a)},onSelectionChange:function(e){var t=e.getSource();this.getModel("newDataModel").setProperty("/"+t.getParent().getLabel().data("property"),t.getSelected())},onChangeDatePicker:function(e){var t=e.getParameter("valid"),a;if(t){a=e.getSource().getParent().data("property");this.getModel("newDataModel").setProperty("/"+a,e.getSource().getDateValue())}},onEditToggled:function(t){var a=this.getModel("DataDetailModel"),o=this.getModel("objectView"),i=this.getView().getModel(),s,n;if(!t.getParameters("editable").editable){if(o.getProperty("/hasChanges")){o.setProperty("/busy",true);s=this._oDataModel.getData();n=this.getView().getBindingContext().sPath;i.update(n,s,{success:function(){a.getData().DataSet[this._index]=s;a.refresh(true);e.show("Updated successfuly current object");o.setProperty("/hasChanges",false);o.setProperty("/busy",false)}.bind(this),error:function(){o.setProperty("/busy",false);this.navFromObjectToDetail(this._setName,this._oEntity.entityType);e.show("Error")}.bind(this)})}}},onPressCancel:function(e){this.getModel("newDataModel").setData({});if(this._oAddDialog){if(this._oAddDialog.isOpen()){this._oAddDialog.getContent()[0].getFormContainers()[0].removeAllFormElements();this._oAddDialog.close()}}},onPressSave:function(t){var a=this.getModel("newDataModel").getData(),o=this.getModel("DataDetailModel"),i=this.getModel();i.create("/"+this._setName,a,{success:function(t,a){o.getData().DataSet.push(t);o.refresh(true);e.show("Successfully added a new Object")}});i.submitChanges({success:function(){this.onNavBack()}.bind(this),error:function(){e.show("Error")}});if(this._oAddDialog){if(this._oAddDialog.isOpen()){this._oAddDialog.getContent()[0].getFormContainers()[0].removeAllFormElements();this._oAddDialog.close()}}this.getModel("newDataModel").setData({})},navFromObjectToDetail:function(e,t){var o=!a.system.phone;this.getModel("appView").setProperty("/layout","TwoColumnsMidExpanded");this.getRouter().navTo("object",{SetName:e,EntityName:t},o)}})});