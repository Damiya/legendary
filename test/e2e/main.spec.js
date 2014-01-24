describe('E2E: main page', function () {
    'use strict';

    var ptor;

    beforeEach(function () {
        browser.get('/');
        ptor = protractor.getInstance();
    });

    it('should load the home page', function () {
        var ele = by.id('home');
        expect(ptor.isElementPresent(ele)).toBe(true);
    });

    // Our tests go in here
});
