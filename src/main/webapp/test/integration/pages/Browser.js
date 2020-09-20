sap.ui.define(["sap/ui/test/Opa5"],function(e){"use strict";e.createPageObjects({onTheBrowserPage:{actions:{iChangeTheHashToObjectN:function(t){return this.waitFor(this.createAWaitForAnEntitySet({entitySet:"Objects",success:function(s){e.getHashChanger().setHash("/Categories/"+s[t].CategoryID)}}))},iChangeTheHashToTheRememberedItem:function(){return this.waitFor({success:function(){var t=this.getContext().currentItem.id;e.getHashChanger().setHash("/Categories/"+t)}})},iChangeTheHashToSomethingInvalid:function(){return this.waitFor({success:function(){e.getHashChanger().setHash("/somethingInvalid")}})}},assertions:{iShouldSeeTheHashForObjectN:function(t){return this.waitFor(this.createAWaitForAnEntitySet({entitySet:"Objects",success:function(s){var a=e.getHashChanger(),r=a.getHash();e.assert.strictEqual(r,"Categories/"+s[t].CategoryID,"The Hash is correct")}}))},iShouldSeeTheHashForTheRememberedObject:function(){return this.waitFor({success:function(){var t=this.getContext().currentItem.id,s=e.getHashChanger(),a=s.getHash();e.assert.strictEqual(a,"Categories/"+t,"The Hash is not correct")}})},iShouldSeeAnEmptyHash:function(){return this.waitFor({success:function(){var t=e.getHashChanger(),s=t.getHash();e.assert.strictEqual(s,"","The Hash should be empty")},errorMessage:"The Hash is not Correct!"})}}}})});