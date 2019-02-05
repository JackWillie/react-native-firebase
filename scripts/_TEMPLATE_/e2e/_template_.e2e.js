describe('_template_()', () => {
  describe('namespace', () => {
    it('accessible from firebase.app()', () => {
      const app = firebase.app();
      should.exist(app._template_);
      app._template_().app.should.equal(app);
    });

    // removing as pending if module.options.hasMultiAppSupport = true
    xit('supports multiple apps', async () => {
      firebase._template_().app.name.should.equal('[DEFAULT]');

      firebase
        ._template_(firebase.app('secondaryFromNative'))
        .app.name.should.equal('secondaryFromNative');

      firebase
        .app('secondaryFromNative')
        ._template_()
        .app.name.should.equal('secondaryFromNative');
    });
  });

  describe('aMethod()', () => {
    // TODO
  });
});
