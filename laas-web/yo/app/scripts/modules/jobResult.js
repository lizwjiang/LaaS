LaaS.module('JobResult', function(JobResult, LaaS, Backbone, Marionette) {
    'use strict';

    JobResult.JobResultView = Marionette.ItemView.extend({
        initialize: function (options) {
            if(options.sync === true){
                this.sync = true;
                this.jobRunning = options.model.attributes;
            }
        },
        template: function(){
            var template = JST['app/handlebars/job/result'];
            var html = template();
            //debug
            return html;
        } ,
        serializeData: function(){
            return {};
        },
        onRender: function(){
            if(this.sync === true){
                this.$('#content-placeholder').html(this.jobRunning.desc).text();
                this.$('#tree').jstree();
            }else{
                this.$('#content-placeholder').html('<h2 class="ui header">Your job is running in the background, please check your inbox later</div>');
            }
        }
    });

    var JobResultController = Marionette.Controller.extend({
        showJobResult: function(job_running_id){
            $.when(LaaS.request('jobResult:entity',{id:job_running_id}))
                .done(function (jobRunningResult) {
                    var jobResultView = new LaaS.JobResult.JobResultView({model:jobRunningResult,sync:true});
                    LaaS.mainRegion.show(jobResultView);
                });
            LaaS.navigate('/jobRunnings/'+job_running_id+"/result");
        }
    });

    LaaS.addInitializer(function () {
        new Marionette.AppRouter({
            appRoutes: {
                'jobResults/:id(/)': 'showJobResult',
                'jobRunnings/:id/result':'showJobResult'
            },
            controller: new JobResultController()
        });
    });
});
