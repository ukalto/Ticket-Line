context('add message', () => {
    let newsEntryText = 'msg' + new Date().getTime();

    it('login', () => {
        cy.loginAdmin();
    });
    it('create news entry', () => {
        cy.createNewsEntry(newsEntryText);
    })

});
