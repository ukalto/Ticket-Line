Cypress.Commands.add('loginAdmin', () => {
    cy.fixture('settings').then(settings => {
        cy.visit(settings.baseUrl);
        cy.contains('button', 'Login').click();
        cy.get('input[formControlName="email"]').type(settings.adminUser);
        cy.get('input[formControlName="password"]').type(settings.adminPw);
        cy.contains('button', 'Login').click();
    })
})

Cypress.Commands.add('createNewsEntry', (msg) => {
    cy.fixture('settings').then(settings => {
		cy.wait(1500);
		
		cy.contains('button','Create News').click();
		cy.get('input[formControlName="title"]').type('title');
		cy.get('textarea[formControlName="summary"]').type('summary');
		cy.get('textarea[formControlName="contents"]').type('content content');
		cy.get('mat-select[formControlName="eventId"]').click().get('mat-option').contains('The HELLA MEGA Tour').click();
		cy.contains('button','Create').click();
    })
})
